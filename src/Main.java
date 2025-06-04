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
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Покупки", "Купить продукты", Status.NEW);
        Task task2 = new Task("Тренировка", "Сходить в зал", Status.IN_PROGRESS);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Проект", "Разработка нового функционала");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Разработка интерфейса", "Создать макет UI", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Тестирование", "Проверить функционал", Status.DONE, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        System.out.println("\n=== Просмотр задач ===");
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getTaskById(task2.getId());

        printAllTasks(taskManager);

        System.out.println("\n=== Создаем дополнительные задачи ===");
        for (int i = 3; i <= 12; i++) {
            Task task = new Task("Задача " + i, "Описание " + i, Status.NEW);
            taskManager.addTask(task);
            taskManager.getTaskById(task.getId()); 
        }

        System.out.println("\n=== История после 12 просмотров ===");
        printHistory(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\n=== Все задачи ===");
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("\nЭпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
            for (Task subtask : manager.getSubtasksOfEpic(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }
        System.out.println("\nПодзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("\nИстория просмотров:");
        printHistory(manager);
    }

    private static void printHistory(TaskManager manager) {
        List<Task> history = manager.getHistory();
        if (history.isEmpty()) {
            System.out.println("История пуста");
        } else {
            System.out.println("Последние " + history.size() + " просмотренных задач:");
            for (Task task : history) {
                System.out.println(" - " + task.getTitle() + " (ID: " + task.getId() + ")");
            }
        }
    }
}
