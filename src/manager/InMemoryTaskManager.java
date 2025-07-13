package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int idCounter = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo))
            .thenComparingInt(Task::getId));

    protected int generateId() {
        return idCounter++;
    }

    @Override
    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicFields(epic);
        prioritizedTasks.add(subtask);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            prioritizedTasks.remove(tasks.get(task.getId()));
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicFields(epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            updateEpicFields(epics.get(subtask.getEpicId()));
        }
    }

    @Override
    public void removeTaskById(int id) {
        prioritizedTasks.remove(tasks.remove(id));
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subId : epic.getSubtaskIds()) {
                subtasks.remove(subId);
                prioritizedTasks.removeIf(t -> t.getId() == subId);
                historyManager.remove(subId);
            }
        }
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskId(id);
            updateEpicFields(epic);
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);
        }
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            for (int subId : epic.getSubtaskIds()) {
                historyManager.remove(subId);
                prioritizedTasks.removeIf(t -> t.getId() == subId);
            }
            historyManager.remove(epic.getId());
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskId(subtask.getId());
            updateEpicFields(epic);
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return List.of();
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    private void updateEpicFields(Epic epic) {
        List<Subtask> subtasksOfEpic = getSubtasksOfEpic(epic.getId());
        if (subtasksOfEpic.isEmpty()) {
            epic.setStatus(Status.NEW);
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = Duration.ZERO;

        for (Subtask s : subtasksOfEpic) {
            if (s.getStatus() != Status.NEW) allNew = false;
            if (s.getStatus() != Status.DONE) allDone = false;

            if (s.getStartTime() != null) {
                if (earliestStart == null || s.getStartTime().isBefore(earliestStart)) {
                    earliestStart = s.getStartTime();
                }
                LocalDateTime end = s.getEndTime();
                if (latestEnd == null || (end != null && end.isAfter(latestEnd))) {
                    latestEnd = end;
                }
            }

            if (s.getDuration() != null) {
                totalDuration = totalDuration.plus(s.getDuration());
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        epic.setStartTime(earliestStart);
        epic.setEndTime(latestEnd);
        epic.setDuration(totalDuration);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}
