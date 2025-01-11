package test.service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {

        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addTaskHistoryManager() {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату");
        historyManager.addTask(snowRemoval);
        final List<Task> history = historyManager.getHistory();
        Task snowRemovalHistory = history.get(snowRemoval.getId());
        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(1, history.size(), "История не должна быть пустой.");
        assertEquals(snowRemoval.getId(),snowRemovalHistory.getId(), "id не совпал");
        assertEquals(snowRemoval.getName(),snowRemovalHistory.getName(), "Имя не совпал");
        assertEquals(snowRemoval.getDescription(),snowRemovalHistory.getDescription(), "Описание не совпал");
        assertEquals(snowRemoval.getStatus(),snowRemovalHistory.getStatus(), "id не совпал");
    }

    @Test
    void addEpicHistoryManager() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы");
        historyManager.addTask(vacationTrip);
        final List<Task> history = historyManager.getHistory();
        Task vacationTripHistory = history.get(vacationTrip.getId());
        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(1, history.size(), "История не должна быть пустой.");
        assertEquals(vacationTrip.getId(),vacationTripHistory.getId(), "id не совпал");
        assertEquals(vacationTrip.getName(),vacationTripHistory.getName(), "Имя не совпал");
        assertEquals(vacationTrip.getDescription(),vacationTripHistory.getDescription(), "Описание не совпал");
        assertEquals(vacationTrip.getStatus(),vacationTripHistory.getStatus(), "id не совпал");
    }

    @Test
    void addSubtaskHistoryManager() {
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", 1);
        historyManager.addTask(travelPlan);
        final List<Task> history = historyManager.getHistory();
        Task travelPlanHistory = history.get(travelPlan.getId());
        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(1, history.size(), "История не должна быть пустой.");
        assertEquals(travelPlan.getId(),travelPlanHistory.getId(), "id не совпал");
        assertEquals(travelPlan.getName(),travelPlanHistory.getName(), "Имя не совпал");
        assertEquals(travelPlan.getDescription(),travelPlanHistory.getDescription(), "Описание не совпал");
        assertEquals(travelPlan.getStatus(),travelPlanHistory.getStatus(), "id не совпал");
    }

}