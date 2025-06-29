import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Task1", "Desc", Status.NEW);
        Task task2 = new Task("Task2", "Desc", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Epic1", "Desc");
        Epic epic2 = new Epic("Epic2", "Desc");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Sub1", "Desc", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Sub2", "Desc", Status.NEW, epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getTaskById(task1.getId());
        manager.getSubtaskById(subtask1.getId());
        printHistory(manager);

        manager.removeTaskById(task1.getId());
        printHistory(manager);

        manager.removeEpicById(epic1.getId());
        printHistory(manager); // []
    }

    private static void printHistory(TaskManager manager) {
        List<Task> history = manager.getHistory();
        if (history.isEmpty()) {
            System.out.println("History is empty");
        } else {
            System.out.println("\nView history (" + history.size() + " items):");
            for (int i = 0; i < history.size(); i++) {
                Task task = history.get(i);
                System.out.println((i + 1) + ". " + task.getTitle() + " [ID:" + task.getId() + "]");
            }
        }
    }
}
