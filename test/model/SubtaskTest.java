package test.model;

import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    public void testSubtaskCreation() {
        Subtask subtask = new Subtask("Test", "Description", Status.NEW, 1);
        assertEquals("Test", subtask.getTitle());
        assertEquals("Description", subtask.getDescription());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(1, subtask.getEpicId());
    }

}