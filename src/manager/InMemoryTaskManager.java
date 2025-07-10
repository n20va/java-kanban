package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo))
            .thenComparingInt(Task::getId));
    protected final List<Task> history = new ArrayList<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    protected int nextId = 1;

    @Override
    public void addTask(Task task) {
        if (task == null) return;
        if (task.getId() == 0) {
            task.setId(nextId++);
        }
        if (checkTimeIntersection(task)) {
            throw new IllegalArgumentException("Task time intersects with existing task");
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null) return;
        if (epic.getId() == 0) {
            epic.setId(nextId++);
        }
        epics.put(epic.getId(), epic);
        updateEpicTimeAndDuration(epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) return;
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Epic does not exist for subtask");
        }
        if (subtask.getId() == 0) {
            subtask.setId(nextId++);
        }
        if (checkTimeIntersection(subtask)) {
            throw new IllegalArgumentException("Subtask time intersects with existing task");
        }
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic);
        updateEpicTimeAndDuration(epic);
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
        if (task != null) addToHistory(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) addToHistory(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) addToHistory(subtask);
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) return;
        if (checkTimeIntersection(task, task.getId())) {
            throw new IllegalArgumentException("Task time intersects with existing task");
        }
        prioritizedTasks.remove(tasks.get(task.getId()));
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) return;
        epics.put(epic.getId(), epic);
        updateEpicTimeAndDuration(epic);
    }
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) return;
        if (checkTimeIntersection(subtask, subtask.getId())) {
            throw new IllegalArgumentException("Subtask time intersects with existing task");
        }
        prioritizedTasks.remove(subtasks.get(subtask.getId()));
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
        updateEpicTimeAndDuration(epic);
    }

    @Override
    public void removeTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            removeFromHistory(task);
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.remove(subId);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                    removeFromHistory(subtask);
                }
            }
            removeFromHistory(epic);
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
                updateEpicTimeAndDuration(epic);
            }
            removeFromHistory(subtask);
        }
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            for (int subId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.remove(subId);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                }
            }
            removeFromHistory(epic);
        }
        epics.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
            updateEpicTimeAndDuration(epic);
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return Collections.emptyList();
        List<Subtask> list = new ArrayList<>();
        for (int id : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(id);
            if (subtask != null) {
                list.add(subtask);
            }
        }
        return list;
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean checkTimeIntersection(Task newTask) {
        return checkTimeIntersection(newTask, -1);
    }

    private boolean checkTimeIntersection(Task newTask, int ignoreId) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }
        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();

        for (Task task : prioritizedTasks) {
            if (task.getId() == ignoreId) continue;
            if (task.getStartTime() == null || task.getDuration() == null) continue;
            LocalDateTime start = task.getStartTime();
            LocalDateTime end = task.getEndTime();

            boolean intersect = newStart.isBefore(end) && newEnd.isAfter(start);
            if (intersect) {
                return true;
            }
        }
        return false;
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasksOfEpic = getSubtasksOfEpic(epic.getId());
        if (subtasksOfEpic.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = subtasksOfEpic.stream().allMatch(s -> s.getStatus() == Status.NEW);
        boolean allDone = subtasksOfEpic.stream().allMatch(s -> s.getStatus() == Status.DONE);

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void updateEpicTimeAndDuration(Epic epic) {
        List<Subtask> subtasksOfEpic = getSubtasksOfEpic(epic.getId());

        if (subtasksOfEpic.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            return;
        }

        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Subtask sub : subtasksOfEpic) {
            if (sub.getDuration() != null) {
                totalDuration = totalDuration.plus(sub.getDuration());
            }
            if (sub.getStartTime() != null) {
                if (earliestStart == null || sub.getStartTime().isBefore(earliestStart)) {
                    earliestStart = sub.getStartTime();
                }
                LocalDateTime subEnd = sub.getEndTime();
                if (subEnd != null && (latestEnd == null || subEnd.isAfter(latestEnd))) {
                    latestEnd = subEnd;
                }
            }
        }

        epic.setDuration(totalDuration);
        epic.setStartTime(earliestStart);
    }

    private void addToHistory(Task task) {
        history.remove(task);
        history.add(task);
        if (history.size() > 10) {
            history.remove(0);
        }
    }

    private void removeFromHistory(Task task) {
        history.remove(task);
    }
}