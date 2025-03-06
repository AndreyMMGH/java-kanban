package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    public void tasksWithTheSameIdShouldBeEqual() {
        Task snowRemoval = new Task(5, "Почистить снег", "Для чистки взять новую лопату", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task waterTheFlowers = new Task(5, "Полить цветы", "Для полива использовать лейку", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(10));

        assertEquals(snowRemoval, waterTheFlowers, "Возникла проблема! При одинаковом id задачи должны быть равны");
    }
}