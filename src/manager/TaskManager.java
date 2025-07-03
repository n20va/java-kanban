package manager;

import model.Task;
import model.Epic;
import model.Subtask;

import java.util.List;

public interface TaskManager {

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    List<Subtask> getSubtasksOfEpic(int epicId);

    List<Task> getHistory();
}