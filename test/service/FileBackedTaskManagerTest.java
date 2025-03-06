package service;

import org.junit.jupiter.api.Test;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void saveAndRestoreTasks() throws IOException {
        File file = File.createTempFile("task", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager initialManager = new FileBackedTaskManager(file);

        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now(), Duration.ofMinutes(30));
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку", LocalDateTime.now(), Duration.ofMinutes(10));
        int idSnowRemoval = initialManager.addNewTask(snowRemoval);
        int idWaterTheFlowers = initialManager.addNewTask(waterTheFlowers);

        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        int idVacationTrip = initialManager.addNewEpic(vacationTrip);

        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now(), Duration.ofMinutes(120));
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip, LocalDateTime.now().plusMinutes(130), Duration.ofMinutes(100));
        int idTravelPlan = initialManager.addNewSubtask(travelPlan);
        int idHotelBooking = initialManager.addNewSubtask(hotelBooking);

        initialManager.getTask(idSnowRemoval);
        initialManager.getTask(idWaterTheFlowers);
        initialManager.getEpic(idVacationTrip);
        initialManager.getSubtask(idTravelPlan);
        initialManager.getSubtask(idHotelBooking);

        FileBackedTaskManager subsequentManager = FileBackedTaskManager.loadFromFile(file);

        Task subsequentSnowRemoval = subsequentManager.getTask(idSnowRemoval);
        Task subsequentWaterTheFlowers = subsequentManager.getTask(idWaterTheFlowers);
        Epic subsequentVacationTrip = subsequentManager.getEpic(idVacationTrip);
        Subtask subsequentTravelPlan = subsequentManager.getSubtask(idTravelPlan);
        Subtask subsequentHotelBooking = subsequentManager.getSubtask(idHotelBooking);

        assertEquals(snowRemoval.getName(), subsequentSnowRemoval.getName(), "Имя у subsequentSnowRemoval должно быть таким же, как у snowRemoval");
        assertEquals(snowRemoval.getDescription(), subsequentSnowRemoval.getDescription(), "Описание у subsequentSnowRemoval должно быть таким же, как у snowRemoval");
        assertEquals(snowRemoval.getStatus(), subsequentSnowRemoval.getStatus(), "Статус у subsequentSnowRemoval должен быть таким же, как у snowRemoval");

        assertEquals(waterTheFlowers.getName(), subsequentWaterTheFlowers.getName(), "Имя у subsequentWaterTheFlowers должно быть таким же, как у waterTheFlowers");
        assertEquals(waterTheFlowers.getDescription(), subsequentWaterTheFlowers.getDescription(), "Описание у subsequentWaterTheFlowers должно быть таким же, как у waterTheFlowers");
        assertEquals(waterTheFlowers.getStatus(), subsequentWaterTheFlowers.getStatus(), "Статус у subsequentWaterTheFlowers должен быть таким же, как у waterTheFlowers");

        assertEquals(vacationTrip.getName(), subsequentVacationTrip.getName(), "Имя у subsequentVacationTrip должно быть таким же, как у vacationTrip");
        assertEquals(vacationTrip.getDescription(), subsequentVacationTrip.getDescription(), "Описание у subsequentVacationTrip должно быть таким же, как у vacationTrip");
        assertEquals(vacationTrip.getStatus(), subsequentVacationTrip.getStatus(), "Статус у subsequentVacationTrip должен быть таким же, как у vacationTrip");
        assertEquals(vacationTrip.getSubtaskIds(), subsequentVacationTrip.getSubtaskIds(), "Список подзадач у subsequentVacationTrip должен быть таким же, как у vacationTrip");

        assertEquals(travelPlan.getName(), subsequentTravelPlan.getName(), "Имя у subsequentTravelPlan должно быть таким же, как у travelPlan");
        assertEquals(travelPlan.getDescription(), subsequentTravelPlan.getDescription(), "Описание у subsequentTravelPlan должно быть таким же, как у travelPlan");
        assertEquals(travelPlan.getStatus(), subsequentTravelPlan.getStatus(), "Статус у subsequentTravelPlan должен быть таким же, как у travelPlan");
        assertEquals(travelPlan.getEpicId(), subsequentTravelPlan.getEpicId(), "epicId у subsequentTravelPlan должен быть таким же, как у travelPlan");
        assertEquals(vacationTrip.getId(), subsequentTravelPlan.getEpicId(), "epicId у subsequentTravelPlan должен быть таким же, как id у vacationTrip");

        assertEquals(hotelBooking.getName(), subsequentHotelBooking.getName(), "Имя у subsequentHotelBooking должно быть таким же, как у hotelBooking");
        assertEquals(hotelBooking.getDescription(), subsequentHotelBooking.getDescription(), "Описание у subsequentHotelBooking должно быть таким же, как у hotelBooking");
        assertEquals(hotelBooking.getStatus(), subsequentHotelBooking.getStatus(), "Статус у subsequentHotelBooking должен быть таким же, как у hotelBooking");
        assertEquals(hotelBooking.getEpicId(), subsequentHotelBooking.getEpicId(), "epicId у subsequentHotelBooking должен быть таким же, как у hotelBooking");
        assertEquals(vacationTrip.getId(), subsequentHotelBooking.getEpicId(), "epicId у subsequentHotelBooking должен быть таким же, как id у vacationTrip");

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


