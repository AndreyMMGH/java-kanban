package test.model;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    public void tasksWithTheSameIdShouldBeEqual() {
        Task snowRemoval = new Task(5, "Почистить снег", "Для чистки взять новую лопату", Status.NEW);
        Task waterTheFlowers = new Task(5, "Полить цветы", "Для полива использовать лейку", Status.IN_PROGRESS);

        assertEquals(snowRemoval, waterTheFlowers, "Возникла проблема! При одинаковом id задачи должны быть равны");
    }
}