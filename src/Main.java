import java.util.*;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = manager.createTask(new Task("Переезд", "Собрать вещи и переехать", Status.NEW));
        Task task2 = manager.createTask(new Task("Купить ноутбук", "Выбрать и купить новый ноутбук", Status.NEW));

        Epic epic1 = manager.createEpic(new Epic("Организовать праздник", "Подготовить семейный праздник"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Купить продукты", "Купить еду и напитки", Status.NEW, epic1.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Пригласить гостей", "Отправить приглашения", Status.NEW, epic1.getId()));

        Epic epic2 = manager.createEpic(new Epic("Ремонт квартиры", "Переобустроить квартиру"));
        Subtask subtask3 = manager.createSubtask(new Subtask("Купить краску", "Выбрать и купить краску", Status.NEW, epic1.getId()));
        // Печать всех сущностей
        printAll(manager);

        // Обновление статусов
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);

        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        subtask2.setStatus(Status.NEW);
        manager.updateSubtask(subtask2);

        subtask3.setStatus(Status.DONE);
        manager.updateSubtask(subtask3);

        System.out.println("\nПосле изменения статусов:");
        printAll(manager);

        // Удаления
        manager.deleteTaskById(task2.getId());
        manager.deleteEpicById(epic1.getId());

        System.out.println("\nПосле удаления задачи и эпика:");
        printAll(manager);
    }

    private static void printAll(TaskManager manager) {
        System.out.println("Все задачи:");
        for (Task t : manager.getAllTasks()) {
            System.out.println(t);
        }

        System.out.println("\nВсе эпики:");
        for (Epic e : manager.getAllEpics()) {
            System.out.println(e);
        }

        System.out.println("\nВсе подзадачи:");
        for (Subtask s : manager.getAllSubtasks()) {
            System.out.println(s + " (Epic ID: " + s.getEpicId() + ")");
        }
        System.out.println("---------------------------------------------------");
    }
}