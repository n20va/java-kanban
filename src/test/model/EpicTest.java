package test.model;

import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    private Epic epic;
    private Subtask s1;
    private Subtask s2;

    @BeforeEach
    public void setUp() {
        epic = new Epic("Epic Title", "Epic Description");
    }

    @Test
    public void shouldReturnNewStatusIfAllSubtasksNew() {
        Subtask s1 = new Subtask("Sub1", "Desc1", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 7, 1, 10, 0));
        Subtask s2 = new Subtask("Sub2", "Desc2", Status.NEW, epic.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 7, 1, 12, 0));

        epic.addSubtaskId(s1.getEpicId());
        epic.addSubtaskId(s2.getEpicId());

        assertEquals(Status.NEW, epic.getStatus());
    }



    @Test
    void epicIsCreatedWithEmptySubtaskList() {
        assertNotNull(epic.getSubtaskIds());
        assertTrue(epic.getSubtaskIds().isEmpty());
    }


}