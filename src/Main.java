import manager.FileBackedTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");
        TaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Планирование отпуска", "Выбрать направление и даты", Status.NEW);
        Task task2 = new Task("Закупка продуктов", "Купить продукты на неделю", Status.IN_PROGRESS);
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Организация мероприятия", "Подготовка к корпоративу");
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Бронирование зала", "Выбрать и забронировать помещение", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Закупка еды", "Заказать еду и напитки", Status.NEW, epic1.getId());

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());

        printAll(manager);
        System.out.println("\nВосстановление менеджера из файла:");
        TaskManager restoredManager = FileBackedTaskManager.loadFromFile(file);
        printAll(restoredManager);
    }

    private static void printAll(TaskManager manager) {
        System.out.println("\n=== Задачи ===");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\n=== Эпики ===");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\n=== Подзадачи ===");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\n=== История просмотров ===");
        for (Task task : manager.getHistory()) {
            System.out.println(task);

        }
    }
}
