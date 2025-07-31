package test.model;

import model.Status;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private Subtask subtask;

    @BeforeEach
    void setUp() {
        subtask = new Subtask("Subtask 1", "Subtask Description", Status.IN_PROGRESS, 10, Duration.ZERO, null);
        subtask.setId(2);
        subtask.setDuration(Duration.ofMinutes(45));
        subtask.setStartTime(LocalDateTime.of(2025, 7, 10, 11, 0));
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(2, subtask.getId());
        assertEquals("Subtask 1", subtask.getTitle());
        assertEquals("Subtask Description", subtask.getDescription());
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
        assertEquals(10, subtask.getEpicId());
        assertEquals(Duration.ofMinutes(45), subtask.getDuration());
        assertEquals(LocalDateTime.of(2025, 7, 10, 11, 0), subtask.getStartTime());
    }

    @Test
    void testGetEndTimeCalculation() {
        LocalDateTime expectedEnd = subtask.getStartTime().plus(subtask.getDuration());
        assertEquals(expectedEnd, subtask.getEndTime());
    }

    @Test
    void testToStringContainsEpicId() {
        String toString = subtask.toString();
        assertTrue(toString.contains("epicId=10"));
    }
}