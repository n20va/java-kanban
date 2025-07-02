package test.model;

import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void setUp() {
        epic = new Epic("Epic Title", "Epic Description");

        subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, epic.getId());

        subtask1.setId(1);
        subtask2.setId(2);
    }

    @Test
    void epicIsCreatedWithEmptySubtaskList() {
        assertNotNull(epic.getSubtaskIds());
        assertTrue(epic.getSubtaskIds().isEmpty());
    }

    @Test
    void addSubtask_addsIdToList() {
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        assertEquals(2, epic.getSubtaskIds().size());
        assertTrue(epic.getSubtaskIds().contains(subtask1.getId()));
        assertTrue(epic.getSubtaskIds().contains(subtask2.getId()));
    }

    @Test
    void removeSubtask_removesCorrectId() {
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        epic.removeSubtask(subtask1.getId());

        assertEquals(1, epic.getSubtaskIds().size());
        assertFalse(epic.getSubtaskIds().contains(subtask1.getId()));
        assertTrue(epic.getSubtaskIds().contains(subtask2.getId()));
    }

}