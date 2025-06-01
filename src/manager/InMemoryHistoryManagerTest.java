package manager;

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
    void removeFromHistory() {
        Task task1 = new Task("Task1", "Desc", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task2", "Desc", Status.IN_PROGRESS);
        task2.setId(2);
        Task task3 = new Task("Task3", "Desc", Status.DONE);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertFalse(history.contains(task2));
    }

    @Test
    void addSameTaskMovesItToEnd() {
        Task task1 = new Task("Task1", "Desc", Status.NEW);
        task1.setId(1);

        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }
}
