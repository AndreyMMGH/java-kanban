package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    public void subtasksWithTheSameIdShouldBeEqual () {
        Subtask travelPlan = new Subtask(11,"Составить план поездки", "Выбрать регион и туристические маршруты", Status.NEW, 7);
        Subtask hotelBooking = new Subtask(11, "Забронировать жилье", "Посмотреть гостевые дома и квартиры", Status.DONE, 7);

        assertEquals(travelPlan, hotelBooking, "Возникла проблема! При одинаковом id подзадачи должны быть равны");
    }

}