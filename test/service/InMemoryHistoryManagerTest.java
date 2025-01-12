package service;

import model.Epic;
import model.Subtask;
import model.Task;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

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
        assertEquals(snowRemoval.getName(),snowRemovalHistory.getName(), "Имя не совпало");
        assertEquals(snowRemoval.getDescription(),snowRemovalHistory.getDescription(), "Описание не совпало");
        assertEquals(snowRemoval.getStatus(),snowRemovalHistory.getStatus(), "Статус не совпал");
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
        assertEquals(vacationTrip.getName(),vacationTripHistory.getName(), "Имя не совпало");
        assertEquals(vacationTrip.getDescription(),vacationTripHistory.getDescription(), "Описание не совпало");
        assertEquals(vacationTrip.getStatus(),vacationTripHistory.getStatus(), "Статус не совпал");
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
        assertEquals(travelPlan.getName(),travelPlanHistory.getName(), "Имя не совпало");
        assertEquals(travelPlan.getDescription(),travelPlanHistory.getDescription(), "Описание не совпало");
        assertEquals(travelPlan.getStatus(),travelPlanHistory.getStatus(), "Статус не совпал");
    }

}