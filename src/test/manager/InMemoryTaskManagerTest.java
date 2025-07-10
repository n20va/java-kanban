package test.manager;

import manager.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void testAddAndGetTask() {
        Task task = new Task("Task 1", "Description task 1", Status.NEW, Duration.ZERO, null);
        manager.addTask(task);

        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals(task.getTitle(), tasks.get(0).getTitle());
    }

    @Test
    public void testAddAndGetEpic() {
        Epic epic = new Epic("Epic 1", "Description epic 1");
        manager.addEpic(epic);

        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size());
        assertEquals(epic.getTitle(), epics.get(0).getTitle());
    }

    @Test
    public void testAddAndGetSubtask() {
        Epic epic = new Epic("Epic 1", "Description epic 1");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description subtask 1", Status.NEW, epic.getId(), Duration.ZERO, null);
        manager.addSubtask(subtask);

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(subtask.getTitle(), subtasks.get(0).getTitle());

        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertTrue(updatedEpic.getSubtaskIds().contains(subtask.getId()));
    }

    @Test
    public void testRemoveTask() {
        Task task = new Task("Task 1", "Description task 1", Status.NEW, Duration.ZERO, null);
        manager.addTask(task);

        manager.removeTaskById(task.getId());
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void testRemoveEpicAlsoRemovesSubtasks() {
        Epic epic = new Epic("Epic 1", "Description epic 1");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description subtask 1", Status.NEW, epic.getId(), Duration.ZERO, null);
        manager.addSubtask(subtask);

        manager.removeEpicById(epic.getId());

        assertTrue(manager.getAllEpics().isEmpty());

        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Task 1", "Description task 1", Status.NEW, Duration.ZERO, null);
        manager.addTask(task);

        task.setStatus(Status.DONE);
        manager.updateTask(task);

        Task updated = manager.getTaskById(task.getId());
        assertEquals(Status.DONE, updated.getStatus());
    }
}