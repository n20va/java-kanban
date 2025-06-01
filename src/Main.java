import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Покупки", "Купить продукты", Status.NEW);
        taskManager.addTask(task1);

        Epic epic1 = new Epic("Проект", "Разработка нового функционала");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Разработка интерфейса", "Создать макет UI", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);

        System.out.println("\nЭпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);
        }

    }
}