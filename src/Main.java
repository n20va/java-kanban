import manager.FileBackedTaskManager;
import manager.TaskManager;
import model.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");
        TaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Task 1", "Simple task",
                Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 7, 1, 10, 0));
        Task task2 = new Task("Task 2", "Another task",
                Status.NEW, Duration.ofMinutes(45), LocalDateTime.of(2025, 7, 1, 11, 0));
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic = new Epic("Epic 1", "Epic with subtasks");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Part 1",
                Status.NEW, epic.getId(), Duration.ofMinutes(60), LocalDateTime.of(2025, 7, 1, 9, 0));
        Subtask subtask2 = new Subtask("Subtask 2", "Part 2",
                Status.DONE, epic.getId(), Duration.ofMinutes(90), LocalDateTime.of(2025, 7, 1, 13, 0));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        System.out.println("=== Prioritized Tasks ===");
        for (Task task : manager.getPrioritizedTasks()) {
            System.out.println(task + " | start=" + task.getStartTime() + " | end=" + task.getEndTime());
        }

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println("\n=== Loaded Tasks ===");
        for (Task task : loadedManager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\n=== Loaded Epics ===");
        for (Epic e : loadedManager.getAllEpics()) {
            System.out.println(e);
        }

        System.out.println("\n=== Loaded Subtasks ===");
        for (Subtask s : loadedManager.getAllSubtasks()) {
            System.out.println(s);

        }
    }
}