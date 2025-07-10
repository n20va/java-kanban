package test.manager;

import manager.FileBackedTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    static File file;
    TaskManager manager;

    @BeforeAll
    static void beforeAll() {
        file = new File("test_data.csv");
    }

    @BeforeEach
    void setUp() {
        manager = new FileBackedTaskManager(file);
        manager.clearTasks();
        manager.clearEpics();
        manager.clearSubtasks();
    }

    @AfterEach
    void tearDown() {
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testSaveAndLoad() {
        Task task = new Task("Task 1", "Description 1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 7, 1, 9, 0));
        manager.addTask(task);

        Epic epic = new Epic("Epic 1", "Epic description");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Part 1", Status.NEW,
                epic.getId(), Duration.ofMinutes(60), LocalDateTime.of(2025, 7, 1, 10, 0));
        Subtask subtask2 = new Subtask("Subtask 2", "Part 2", Status.DONE,
                epic.getId(), Duration.ofMinutes(90), LocalDateTime.of(2025, 7, 1, 12, 0));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask1.getId());

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Task loadedTask = loadedManager.getTaskById(task.getId());
        assertNotNull(loadedTask);
        assertEquals(task.getTitle(), loadedTask.getTitle());
        assertEquals(task.getDuration(), loadedTask.getDuration());
        assertEquals(task.getStartTime(), loadedTask.getStartTime());

        Epic loadedEpic = loadedManager.getEpicById(epic.getId());
        assertNotNull(loadedEpic);
        assertEquals(epic.getTitle(), loadedEpic.getTitle());

        List<Subtask> loadedSubtasks = loadedManager.getSubtasksOfEpic(epic.getId());
        assertEquals(2, loadedSubtasks.size());

        Subtask loadedSubtask1 = loadedManager.getSubtaskById(subtask1.getId());
        assertEquals(subtask1.getDuration(), loadedSubtask1.getDuration());
        assertEquals(subtask1.getStartTime(), loadedSubtask1.getStartTime());

        List<Task> history = loadedManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task.getId(), history.get(0).getId());
        assertEquals(epic.getId(), history.get(1).getId());
        assertEquals(subtask1.getId(), history.get(2).getId());
    }

    @Test
    public void testLoadFromCorruptedFile() {
        try {
            java.nio.file.Files.writeString(file.toPath(), "Not a valid CSV content\n");
        } catch (Exception e) {
            fail("Не удалось записать тестовый файл");
        }

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertNotNull(loadedManager);
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
        assertTrue(loadedManager.getHistory().isEmpty());
    }

}