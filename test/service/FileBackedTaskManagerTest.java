package service;

import org.junit.jupiter.api.Test;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void saveAndRestoreTasks() throws IOException {
        File file = File.createTempFile("task", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager initialManager = new FileBackedTaskManager(file);

        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату");
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку");
        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы");
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", 3);
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", 3);

        int idSnowRemoval = initialManager.addNewTask(snowRemoval);
        int idWaterTheFlowers = initialManager.addNewTask(waterTheFlowers);
        int idVacationTrip = initialManager.addNewEpic(vacationTrip);
        int idTravelPlan = initialManager.addNewSubtask(travelPlan);
        int idHotelBooking = initialManager.addNewSubtask(hotelBooking);

        initialManager.getTask(idSnowRemoval);
        initialManager.getTask(idWaterTheFlowers);
        initialManager.getEpic(idVacationTrip);
        initialManager.getSubtask(idTravelPlan);
        initialManager.getSubtask(idHotelBooking);

        FileBackedTaskManager subsequentManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(initialManager.getTask(idSnowRemoval), subsequentManager.getTask(idSnowRemoval), "Задача snowRemoval из разных менеджеров должна совпасть");
        assertEquals(initialManager.getTask(idWaterTheFlowers), subsequentManager.getTask(idWaterTheFlowers), "Задача waterTheFlowers из разных менеджеров должна совпасть");
        assertEquals(initialManager.getEpic(idVacationTrip), subsequentManager.getEpic(idVacationTrip), "Эпик vacationTrip из разных менеджеров должна совпасть");
        assertEquals(initialManager.getSubtask(idTravelPlan), subsequentManager.getSubtask(idTravelPlan), "Подзадача travelPlan из разных менеджеров должна совпасть");
        assertEquals(initialManager.getSubtask(idHotelBooking), subsequentManager.getSubtask(idHotelBooking), "Подзадача hotelBooking из разных менеджеров должна совпасть");

        List<Task> initialIdsHistory = initialManager.historyManager.getHistory();
        List<Task> subsequentIdsHistory = subsequentManager.historyManager.getHistory();
        assertEquals(initialIdsHistory.size(), subsequentIdsHistory.size(), "id истории должны совпасть по количеству");
    }

    @Test
    void saveAndLoadIntoEmptyFile() throws IOException {
        File file = File.createTempFile("task", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager initialManager = new FileBackedTaskManager(file);

        initialManager.save();

        FileBackedTaskManager subsequentManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(initialManager.getTasks().size(), subsequentManager.getTasks().size(), "После сохранения и восстановления количество задач должно совпасть");
        assertEquals(initialManager.getEpics().size(), subsequentManager.getEpics().size(), "После сохранения и восстановления количество эпиков должно совпасть");
        assertEquals(initialManager.getSubtasks().size(), subsequentManager.getSubtasks().size(), "После сохранения и восстановления количество подзадач должно совпасть");

        List<Task> initialIdsHistory = initialManager.historyManager.getHistory();
        List<Task> subsequentIdsHistory = subsequentManager.historyManager.getHistory();
        assertEquals(initialIdsHistory.size(), subsequentIdsHistory.size(), "id истории должны совпасть по количеству");
    }
}


