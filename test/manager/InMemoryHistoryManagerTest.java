package test.manager;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import model.Task;
import model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addAndGetHistory() {
        Task task1 = new Task("Task1", "Desc", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task2", "Desc", Status.IN_PROGRESS);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void addSameTaskTwice() {
        Task task = new Task("Task", "Desc", Status.NEW);
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task, history.get(0));
        assertEquals(task, history.get(1));
    }

    @Test
    void historyLimit() {
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Task" + i, "Desc", Status.NEW);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size());
        assertEquals(6, history.get(0).getId());
        assertEquals(15, history.get(9).getId());
    }
}