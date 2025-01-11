package test.model;

import model.Epic;
import model.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    public void withTheSameIdEpicsShouldBeEqual() {
        Epic vacationTrip = new Epic(1, "Съездить в отпуск", "Туда, где горы", Status.NEW);
        Epic choosingPpuppy = new Epic(1, "Завести собаку", "Должна быть комнатная порода", Status.DONE);

        assertEquals(vacationTrip, choosingPpuppy, "Возникла проблема! При одинаковом id эпики должны быть равны");
    }

}