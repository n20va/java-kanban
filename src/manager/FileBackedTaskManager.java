package manager;

import model.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл.", e);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",")
                .append(task instanceof Epic ? "EPIC" : task instanceof Subtask ? "SUBTASK" : "TASK").append(",")
                .append(task.getTitle()).append(",")
                .append(task.getStatus()).append(",")
                .append(task.getDescription());

        if (task instanceof Subtask) {
            sb.append(",").append(((Subtask) task).getEpicId());
        } else {
            sb.append(",");
        }
        return sb.toString();
    }

    private Task fromString(String line) {
        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case "TASK":
                Task task = new Task(title, description, status);
                task.setId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setStatus(Status.NEW);
                return epic;
            case "SUBTASK":
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(title, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 1; i < lines.size(); i++) { // пропускаем заголовок
                Task task = manager.fromString(lines.get(i));
                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                }
                if (task.getId() >= manager.nextId) {
                    manager.nextId = task.getId() + 1;
                }
            }
            for (Subtask subtask : manager.subtasks.values()) {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtask(subtask);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла.", e);
        }

        return manager;
    }
}
