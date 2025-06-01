import manager.Managers;
import manager.TaskManager;
import model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Test Task", "This is a test task.");
        manager.addTask(task);

        for (Task t : manager.getTasks()) {
            System.out.println("Task: " + t.getName() + " - " + t.getDescription());
        }
    }
}