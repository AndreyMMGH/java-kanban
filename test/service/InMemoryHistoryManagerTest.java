package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
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
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now(), Duration.ofMinutes(30));
        historyManager.addTask(snowRemoval);
        final List<Task> history = historyManager.getHistory();
        Task snowRemovalHistory = history.getFirst();
        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(1, history.size(), "История не должна быть пустой.");
        assertEquals(snowRemoval.getId(), snowRemovalHistory.getId(), "id не совпал");
        assertEquals(snowRemoval.getName(), snowRemovalHistory.getName(), "Имя не совпало");
        assertEquals(snowRemoval.getDescription(), snowRemovalHistory.getDescription(), "Описание не совпало");
        assertEquals(snowRemoval.getStatus(), snowRemovalHistory.getStatus(), "Статус не совпал");
    }

    @Test
    void addEpicHistoryManager() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы", null, null, null);
        historyManager.addTask(vacationTrip);
        final List<Task> history = historyManager.getHistory();
        Task vacationTripHistory = history.getFirst();
        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(1, history.size(), "История не должна быть пустой.");
        assertEquals(vacationTrip.getId(), vacationTripHistory.getId(), "id не совпал");
        assertEquals(vacationTrip.getName(), vacationTripHistory.getName(), "Имя не совпало");
        assertEquals(vacationTrip.getDescription(), vacationTripHistory.getDescription(), "Описание не совпало");
        assertEquals(vacationTrip.getStatus(), vacationTripHistory.getStatus(), "Статус не совпал");
    }

    @Test
    void addSubtaskHistoryManager() {
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", 1, LocalDateTime.now(), Duration.ofMinutes(120));
        historyManager.addTask(travelPlan);
        final List<Task> history = historyManager.getHistory();
        Task travelPlanHistory = history.getFirst();
        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(1, history.size(), "История не должна быть пустой.");
        assertEquals(travelPlan.getId(), travelPlanHistory.getId(), "id не совпал");
        assertEquals(travelPlan.getName(), travelPlanHistory.getName(), "Имя не совпало");
        assertEquals(travelPlan.getDescription(), travelPlanHistory.getDescription(), "Описание не совпало");
        assertEquals(travelPlan.getStatus(), travelPlanHistory.getStatus(), "Статус не совпал");
    }

    @Test
    void shouldAddTasksToLinkedList() {
        Task snowRemoval = new Task(1, "Почистить снег", "Для чистки взять новую лопату", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task waterTheFlowers = new Task(2, "Полить цветы", "Для полива использовать лейку", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        historyManager.addTask(snowRemoval);
        historyManager.addTask(waterTheFlowers);
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "В истории должно быть 2 элемента");
        assertEquals(snowRemoval, history.get(0), "Первой задачей в списке должна быть snowRemoval");
        assertEquals(waterTheFlowers, history.get(1), "Первой задачей в списке должна быть waterTheFlowers");
    }

    @Test
    void shouldRemoveTask() {
        Epic vacationTrip = new Epic(3, "Съездить в отпуск", "Туда, где горы", Status.NEW, null, null, null);
        Epic choosingPpuppy = new Epic(4, "Завести собаку", "Должна быть комнатная порода", Status.NEW, null, null, null);
        historyManager.addTask(vacationTrip);
        historyManager.addTask(choosingPpuppy);
        historyManager.remove(vacationTrip.getId());
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "В истории должен быть 1 элемент");
        assertEquals(choosingPpuppy, history.get(0), "Первой задачей в списке должна быть choosingPpuppy");
    }

    @Test
    void addDuplicateTasksToLinkedList() {
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", 3, LocalDateTime.now(), Duration.ofMinutes(120));
        historyManager.addTask(travelPlan);
        historyManager.addTask(travelPlan);
        historyManager.addTask(travelPlan);
        historyManager.addTask(travelPlan);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "В истории должен быть 1 элемент");
        assertEquals(travelPlan, history.get(0), "В списке должен быть только travelPlan");
    }

    @Test
    void getEmptyTaskHistory() {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now(), Duration.ofMinutes(30));
        historyManager.addTask(snowRemoval);
        historyManager.remove(snowRemoval.getId());
        final List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой.");
    }

    @Test
    void deleteBeginningOfHistory() {
        Task snowRemoval = new Task(1, "Почистить снег", "Для чистки взять новую лопату", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task waterTheFlowers = new Task(2, "Полить цветы", "Для полива использовать лейку", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Epic vacationTrip = new Epic(3, "Съездить в отпуск", "Туда, где горы", Status.NEW, null, null, null);
        historyManager.addTask(snowRemoval);
        historyManager.addTask(waterTheFlowers);
        historyManager.addTask(vacationTrip);
        historyManager.remove(snowRemoval.getId());
        final List<Task> history = historyManager.getHistory();

        assertEquals(waterTheFlowers, history.get(0), "Должна быть получена задача waterTheFlowers");
        assertEquals(vacationTrip, history.get(1), "Должен быть получен эпик vacationTrip");
        assertEquals(2, history.size(), "В истории должно быть 2 элемента");
    }

    @Test
    void deleteMiddlePartOfHistory() {
        Task snowRemoval = new Task(1, "Почистить снег", "Для чистки взять новую лопату", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task waterTheFlowers = new Task(2, "Полить цветы", "Для полива использовать лейку", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Epic vacationTrip = new Epic(3, "Съездить в отпуск", "Туда, где горы", Status.NEW, null, null, null);
        historyManager.addTask(snowRemoval);
        historyManager.addTask(waterTheFlowers);
        historyManager.addTask(vacationTrip);
        historyManager.remove(waterTheFlowers.getId());
        final List<Task> history = historyManager.getHistory();

        assertEquals(snowRemoval, history.get(0), "Должна быть получена задача snowRemoval");
        assertEquals(vacationTrip, history.get(1), "Должен быть получен эпик vacationTrip");
        assertEquals(2, history.size(), "В истории должно быть 2 элемента");
    }

    @Test
    void deleteTheEndOfHistory() {
        Task snowRemoval = new Task(1, "Почистить снег", "Для чистки взять новую лопату", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task waterTheFlowers = new Task(2, "Полить цветы", "Для полива использовать лейку", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Epic vacationTrip = new Epic(3, "Съездить в отпуск", "Туда, где горы", Status.NEW, null, null, null);
        historyManager.addTask(snowRemoval);
        historyManager.addTask(waterTheFlowers);
        historyManager.addTask(vacationTrip);
        historyManager.remove(vacationTrip.getId());
        final List<Task> history = historyManager.getHistory();

        assertEquals(snowRemoval, history.get(0), "Должна быть получена задача snowRemoval");
        assertEquals(waterTheFlowers, history.get(1), "Должен быть получен эпик waterTheFlowers");
        assertEquals(2, history.size(), "В истории должно быть 2 элемента");
    }

}