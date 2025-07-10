package test.model;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    private Task task;

    @BeforeEach
    public void setUp() {
        task = new Task(
                "Test Task",
                "Description of test task",
                Status.NEW,
                Duration.ofMinutes(120),
                LocalDateTime.of(2025, 7, 10, 9, 0)
        );
    }

    @Test
    public void shouldCreateTaskWithCorrectFields() {
        assertEquals("Test Task", task.getTitle());
        assertEquals("Description of test task", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(Duration.ofMinutes(120), task.getDuration());
        assertEquals(LocalDateTime.of(2025, 7, 10, 9, 0), task.getStartTime());
    }

    @Test
    public void shouldCalculateEndTimeCorrectly() {
        LocalDateTime expectedEndTime = LocalDateTime.of(2025, 7, 10, 11, 0);
        assertEquals(expectedEndTime, task.getEndTime());
    }

    @Test
    public void shouldUpdateStatus() {
        task.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    public void shouldUpdateStartTimeAndDuration() {
        task.setStartTime(LocalDateTime.of(2025, 7, 10, 10, 0));
        task.setDuration(Duration.ofMinutes(30));

        assertEquals(LocalDateTime.of(2025, 7, 10, 10, 0), task.getStartTime());
        assertEquals(Duration.ofMinutes(30), task.getDuration());
        assertEquals(LocalDateTime.of(2025, 7, 10, 10, 30), task.getEndTime());
    }

    @Test
    public void toStringShouldContainAllFields() {
        String str = task.toString();

        assertNotNull(str);
        assertFalse(str.isEmpty());

        assertTrue(str.contains(task.getTitle()));
        assertTrue(str.contains(task.getDescription()));
        assertTrue(str.contains(task.getStatus().toString()));

        assertTrue(str.contains(String.valueOf(task.getDuration().toMinutes())));

        assertTrue(str.contains(task.getStartTime().toString()));
    }
}