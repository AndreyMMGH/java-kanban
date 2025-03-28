package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    public void withTheSameIdEpicsShouldBeEqual() {
        Epic vacationTrip = new Epic(1, "Съездить в отпуск", "Туда, где горы", Status.NEW, null, null, null);
        Epic choosingPpuppy = new Epic(1, "Завести собаку", "Должна быть комнатная порода", Status.DONE, null, null, null);

        assertEquals(vacationTrip, choosingPpuppy, "Возникла проблема! При одинаковом id эпики должны быть равны");
    }

}