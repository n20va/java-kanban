package test.model;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task("Test Task", "Test Description", Status.NEW);
        task.setId(1);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(1, task.getId());
        assertEquals("Test Task", task.getTitle());
        assertEquals("Test Description", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    void testSetters() {
        task.setId(42);
        task.setTitle("Updated Title");
        task.setDescription("Updated Description");
        task.setStatus(Status.IN_PROGRESS);

        assertEquals(42, task.getId());
        assertEquals("Updated Title", task.getTitle());
        assertEquals("Updated Description", task.getDescription());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    void testEquals_sameValues_shouldBeEqual() {
        Task other = new Task("Test Task", "Test Description", Status.NEW);
        other.setId(1);

        assertEquals(task, other);
    }

    @Test
    void testEquals_differentId_shouldNotBeEqual() {
        Task other = new Task("Test Task", "Test Description", Status.NEW);
        other.setId(99);

        assertNotEquals(task, other);
    }

    @Test
    void testEquals_differentTitle_shouldNotBeEqual() {
        Task other = new Task("Different Title", "Test Description", Status.NEW);
        other.setId(1);

        assertNotEquals(task, other);
    }

    @Test
    void testEquals_differentDescription_shouldNotBeEqual() {
        Task other = new Task("Test Task", "Other Description", Status.NEW);
        other.setId(1);

        assertNotEquals(task, other);
    }

    @Test
    void testEquals_differentStatus_shouldNotBeEqual() {
        Task other = new Task("Test Task", "Test Description", Status.DONE);
        other.setId(1);

        assertNotEquals(task, other);
    }

    @Test
    void testHashCode_sameValues_shouldBeEqual() {
        Task other = new Task("Test Task", "Test Description", Status.NEW);
        other.setId(1);

        assertEquals(task.hashCode(), other.hashCode());
    }

    @Test
    void testHashCode_differentValues_shouldNotBeEqual() {
        Task other = new Task("Other", "Other", Status.DONE);
        other.setId(999);

        assertNotEquals(task.hashCode(), other.hashCode());
    }
}