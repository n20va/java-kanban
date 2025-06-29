package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected int nextId = 1;

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    private int generateId() {
        return nextId++;
    }

    @Override
    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            subtask.setId(generateId());
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtask(subtask);
            updateEpicStatus(epicId);
        }
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
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        Epic oldEpic = epics.get(id);
        if (oldEpic == null) {
            throw new IllegalArgumentException("Эпик с id " + id + " не найден");
        }
        oldEpic.setTitle(epic.getTitle());
        oldEpic.setDescription(epic.getDescription());
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (!subtasks.containsKey(id)) {
            throw new IllegalArgumentException("Подзадача с id " + id + " не найдена");
        }

        Subtask existingSubtask = subtasks.get(id);
        if (subtask.getEpicId() != existingSubtask.getEpicId()) {
            throw new IllegalArgumentException("Нельзя изменить epicId подзадачи");
        }

        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic.getId());
        }
    }


    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subId : epic.getSubtaskIds()) {
                subtasks.remove(subId);
            }
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic.getId());
            }
        }
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        List<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subId);
                if (subtask != null) {
                    result.add(subtask);
                }
            }
        }
        return result;
    }


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int id : subtaskIds) {
            Subtask subtask = subtasks.get(id);
            if (subtask == null) continue;

            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}