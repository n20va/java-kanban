package test.manager;


import manager.InMemoryHistoryManager;
import model.Task;
import model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.Duration;
import java.time.LocalDateTime;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;


    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddAndReturnHistory() {
        Task task = new Task("Test task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        task.setId(1);

        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void shouldNotAddDuplicates() {
        Task task = new Task("Task", "Desc", Status.NEW, Duration.ofMinutes(20), LocalDateTime.now());
        task.setId(1);
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveFromHistory() {
        Task task1 = new Task("Task 1", "Desc 1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        task1.setId(1);
        Task task2 = new Task("Task 2", "Desc 2", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }
}

