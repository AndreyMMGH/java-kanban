package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    public void subtasksWithTheSameIdShouldBeEqual() {
        Subtask travelPlan = new Subtask(11, "Составить план поездки", "Выбрать регион и туристические маршруты", Status.NEW, 7, LocalDateTime.now(), Duration.ofMinutes(120));
        Subtask hotelBooking = new Subtask(11, "Забронировать жилье", "Посмотреть гостевые дома и квартиры", Status.DONE, 7, LocalDateTime.now().plusMinutes(130), Duration.ofMinutes(100));

        assertEquals(travelPlan, hotelBooking, "Возникла проблема! При одинаковом id подзадачи должны быть равны");
    }

}