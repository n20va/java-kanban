import java.util.*;

class TaskManager {
    private int nextId = 1;

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    private int generateId() {
        return nextId++;
    }

    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epic.setStatus(Status.NEW);
        epics.put(id, epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Epic с id " + epicId + " не найден");
        }
        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        epic.addSubtaskId(id);
        updateEpicStatus(epic);
        return subtask;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) return tasks.get(id);
        if (epics.containsKey(id)) return epics.get(id);
        if (subtasks.containsKey(id)) return subtasks.get(id);
        return null;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }
    }

    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        } else {
            throw new IllegalArgumentException("Задача с id " + id + " не найдена");
        }
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (!epics.containsKey(id)) {
            throw new IllegalArgumentException("Эпик с id " + id + " не найден");
        }
        Epic oldEpic = epics.get(id);
        epic.setStatus(oldEpic.getStatus());
        epic.getSubtaskIds().clear();
        epic.getSubtaskIds().addAll(oldEpic.getSubtaskIds());
        epics.put(id, epic);
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (!subtasks.containsKey(id)) {
            throw new IllegalArgumentException("Подзадача с id " + id + " не найдена");
        }
        int oldEpicId = subtasks.get(id).getEpicId();
        int newEpicId = subtask.getEpicId();

        if (oldEpicId != newEpicId) {
            Epic oldEpic = epics.get(oldEpicId);
            Epic newEpic = epics.get(newEpicId);
            if (newEpic == null) {
                throw new IllegalArgumentException("Новый эпик с id " + newEpicId + " не найден");
            }
            oldEpic.removeSubtaskId(id);
            updateEpicStatus(oldEpic);

            newEpic.addSubtaskId(id);
            subtasks.put(id, subtask);
            updateEpicStatus(newEpic);
        } else {
            subtasks.put(id, subtask);
            updateEpicStatus(epics.get(newEpicId));
        }
    }

    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            throw new IllegalArgumentException("Задача с id " + id + " не найдена");
        }
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id " + id + " не найден");
        }
        for (int subtaskId : new ArrayList<>(epic.getSubtaskIds())) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new IllegalArgumentException("Подзадача с id " + id + " не найдена");
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtaskId(id);
            updateEpicStatus(epic);
        }
        subtasks.remove(id);
    }

    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return Collections.emptyList();
        List<Subtask> result = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask sub = subtasks.get(subtaskId);
            if (sub != null) result.add(sub);
        }
        return result;
    }

    private void updateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;
        for (int subId : subtaskIds) {
            Subtask sub = subtasks.get(subId);
            if (sub != null) {
                if (sub.getStatus() != Status.NEW) allNew = false;
                if (sub.getStatus() != Status.DONE) allDone = false;
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
