package service;

import http.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TasksManagerTest<T extends TasksManager> {
    protected T manager;

    @Test
    public void epicCanAddItSelfAsSubtask() {
        Epic vacationTrip = new Epic(1, "Съездить в отпуск", "Туда, где горы", Status.NEW, null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now(), Duration.ofMinutes(120));
        manager.addNewSubtask(travelPlan);
        assertFalse(vacationTrip.getSubtaskIds().contains(vacationTrip.getId()), "Эпик не может содержать себя в качестве подзадачи");
    }

    @Test
    public void subtaskIsNotEqualToItsEpic() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now(), Duration.ofMinutes(120));
        manager.addNewSubtask(travelPlan);
        assertNotEquals(idVacationTrip, travelPlan.getId(), "Подзадачу нельзя сделать своим эпиком");
    }

    @Test
    public void addTask() {
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку", LocalDateTime.now(), Duration.ofMinutes(10));
        manager.addNewTask(waterTheFlowers);
        Task recordedTask = manager.getTask(waterTheFlowers.getId());
        assertNotNull(recordedTask, "Такой задачи нет");
        assertEquals(waterTheFlowers, recordedTask, "Задачи разные");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(waterTheFlowers, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addEpic() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы", null, null, null);
        Epic choosingPpuppy = new Epic("Завести собаку", "Должна быть комнатная порода", null, null, null);
        manager.addNewEpic(vacationTrip);
        manager.addNewEpic(choosingPpuppy);
        Epic recordedEpic = manager.getEpic(vacationTrip.getId());
        assertNotNull(recordedEpic, "Такой задачи нет");
        assertEquals(vacationTrip, recordedEpic, "Эпики разные");

        final List<Epic> epics = manager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество задач.");
        assertEquals(choosingPpuppy, epics.get(1), "Задачи не совпадают.");

    }

    @Test
    public void addSubtask() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы", null, null, null);
        Epic choosingPpuppy = new Epic("Завести собаку", "Должна быть комнатная порода", null, null, null);
        int idVacationTrip = manager.addNewEpic(vacationTrip);
        int idChoosingPpuppy = manager.addNewEpic(choosingPpuppy);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now(), Duration.ofMinutes(120));
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip, LocalDateTime.now().plusMinutes(130), Duration.ofMinutes(100));
        Subtask breedSelection = new Subtask("Выбрать породу", "Можно длинношерстную", idChoosingPpuppy, LocalDateTime.now().minusMinutes(210), Duration.ofMinutes(200));
        manager.addNewSubtask(travelPlan);
        manager.addNewSubtask(hotelBooking);
        manager.addNewSubtask(breedSelection);
        Subtask recordedSubtaskTravelPlan = manager.getSubtask(travelPlan.getId());
        Subtask recordedSubtaskHotelBooking = manager.getSubtask(hotelBooking.getId());
        Subtask recordedSubtaskBreedSelection = manager.getSubtask(breedSelection.getId());
        assertNotNull(recordedSubtaskTravelPlan, "Подзадача План путешествия отсутствует");
        assertNotNull(recordedSubtaskHotelBooking, "Подзадача Забронировать жилье отсутствует");
        assertNotNull(recordedSubtaskBreedSelection, "Подзадача Выбрать породу отсутствует");
        assertEquals(travelPlan, recordedSubtaskTravelPlan, "Найденная подзадача План путешествия отличается от исходной");
        assertEquals(hotelBooking, recordedSubtaskHotelBooking, "Найденная подзадача План путешествия отличается от исходной");
        assertEquals(breedSelection, recordedSubtaskBreedSelection, "Найденная подзадача Выбрать породу отличается от исходной");

        final List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество задач.");
        assertEquals(breedSelection, subtasks.get(2), "Задачи не совпадают.");
    }

    @Test
    public void tasksWithGivenIdAndGeneratedIdDoNotConflict() {
        Task snowRemoval = new Task(2, "Почистить снег", "Для чистки взять новую лопату", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        manager.addNewTask(snowRemoval);
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку", LocalDateTime.now().minusMinutes(470), Duration.ofMinutes(10));
        manager.addNewTask(waterTheFlowers);
        assertNotEquals(snowRemoval.getId(), waterTheFlowers.getId(), "Произошел конфликт. Id одинаковые");
    }

    @Test
    public void checksTheImmutabilityOfTheTask() {
        Task waterTheFlowers = new Task(1, "Полить цветы", "Для полива использовать лейку", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.addNewTask(waterTheFlowers);
        Task recordedTask = manager.getTask(waterTheFlowers.getId());
        assertEquals(waterTheFlowers.getId(), recordedTask.getId(), "id не совпал");
        assertEquals(waterTheFlowers.getName(), recordedTask.getName(), "Имя не совпало");
        assertEquals(waterTheFlowers.getDescription(), recordedTask.getDescription(), "Описание не совпало");
        assertEquals(waterTheFlowers.getStatus(), recordedTask.getStatus(), "Статус не совпал");
    }

    @Test
    public void doNotSaveDeletedSubtaskInHistory() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now(), Duration.ofMinutes(120));
        int idTravelPlan = manager.addNewSubtask(travelPlan);
        manager.getEpic(idVacationTrip);
        manager.getSubtask(idTravelPlan);
        manager.deleteSubTask(idTravelPlan);
        final List<Task> history = manager.getHistory();
        for (Task task : history) {
            assertNotEquals(idTravelPlan, task.getId(), "В истории не должна отображаться удаленная подзадача");
        }
    }

    @Test
    public void deletingSubtaskItMustBeRemovedFromTheEpic() {
        Epic vacationTrip = new Epic(1, "Съездить в отпуск", "Туда, где горы", Status.NEW, null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now(), Duration.ofMinutes(120));
        int idTravelPlan = manager.addNewSubtask(travelPlan);
        manager.deleteSubTask(idTravelPlan);
        Epic updatedVacationTrip = manager.getEpic(idVacationTrip);
        assertFalse(updatedVacationTrip.getSubtaskIds().contains(idTravelPlan), "В списке подзадач эпика не должно быть id удаленной подзадачи");
    }

    @Test
    public void allowIdChange() {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now(), Duration.ofMinutes(30));
        int idSnowRemoval = manager.addNewTask(snowRemoval);
        Task taskFound = manager.getTask(idSnowRemoval);
        Integer previousId = taskFound.getId();
        taskFound.setId(950);
        manager.updateTask(taskFound);
        Task newTaskFound = manager.getTask(idSnowRemoval);
        assertNotEquals(previousId, newTaskFound.getId(), "В результате работы этого теста id должны быть разные.");
    }

    @Test
    public void clearHistoryAfterDeletingAllTasks() {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now(), Duration.ofMinutes(30));
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку", LocalDateTime.now().minusMinutes(470), Duration.ofMinutes(10));
        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы", null, null, null);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", 3, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", 3, LocalDateTime.now().plusMinutes(130), Duration.ofMinutes(100));
        int idSnowRemoval = manager.addNewTask(snowRemoval);
        int idWaterTheFlowers = manager.addNewTask(waterTheFlowers);
        int idVacationTrip = manager.addNewEpic(vacationTrip);
        int idTravelPlan = manager.addNewSubtask(travelPlan);
        int idHotelBooking = manager.addNewSubtask(hotelBooking);
        manager.getTask(idSnowRemoval);
        manager.getTask(idWaterTheFlowers);
        manager.getEpic(idVacationTrip);
        manager.getSubtask(idTravelPlan);
        manager.getSubtask(idHotelBooking);
        manager.deleteTasks();
        manager.deleteEpics();
        final List<Task> history = manager.getHistory();
        assertEquals(0, history.size(), "В истории не должно быть элементов");
    }

    @Test
    public void checkingEpicStatusUpdateBasedOnSubtaskStatus() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);

        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip, LocalDateTime.now().minusMinutes(300), Duration.ofMinutes(50));
        manager.addNewSubtask(travelPlan);
        manager.addNewSubtask(hotelBooking);

        manager.updateSubtask(travelPlan);
        manager.updateSubtask(hotelBooking);
        assertEquals(vacationTrip.getStatus(), Status.NEW, "Статус у эпика vacationTrip должен быть NEW");

        travelPlan.setStatus(Status.DONE);
        hotelBooking.setStatus(Status.DONE);
        manager.updateSubtask(travelPlan);
        manager.updateSubtask(hotelBooking);
        assertEquals(vacationTrip.getStatus(), Status.DONE, "Статус у эпика vacationTrip должен быть DONE");

        travelPlan.setStatus(Status.NEW);
        hotelBooking.setStatus(Status.DONE);
        manager.updateSubtask(travelPlan);
        manager.updateSubtask(hotelBooking);
        assertEquals(vacationTrip.getStatus(), Status.IN_PROGRESS, "Статус у эпика vacationTrip должен быть IN_PROGRESS");

        travelPlan.setStatus(Status.IN_PROGRESS);
        hotelBooking.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(travelPlan);
        manager.updateSubtask(hotelBooking);
        assertEquals(vacationTrip.getStatus(), Status.IN_PROGRESS, "Статус у эпика vacationTrip должен быть IN_PROGRESS");
    }

    @Test
    void checkingRelatedEpicForSubtasks() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);

        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip, LocalDateTime.now().minusMinutes(300), Duration.ofMinutes(50));

        int travelPlanId = manager.addNewSubtask(travelPlan);
        int hotelBookingId = manager.addNewSubtask(hotelBooking);

        Subtask receivedTravelPlan = manager.getSubtask(travelPlanId);
        Subtask receivedHotelBooking = manager.getSubtask(hotelBookingId);

        assertEquals(idVacationTrip, receivedTravelPlan.getEpicId(), "У подзадачи travelPlan неверный epicId");
        assertEquals(idVacationTrip, receivedHotelBooking.getEpicId(), "У подзадачи hotelBooking неверный epicId");

        Epic receivedVacationTrip = manager.getEpic(idVacationTrip);
        List<Integer> subtaskIds = receivedVacationTrip.getSubtaskIds();
        assertTrue(subtaskIds.contains(travelPlanId), "Эпик не содержит travelPlan");
        assertTrue(subtaskIds.contains(hotelBookingId), "Эпик не содержит hotelBooking");
    }

    @Test
    void checkForNonOverlappingSubtasks() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);

        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        manager.addNewSubtask(travelPlan);

        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip, LocalDateTime.now(), Duration.ofMinutes(120));

        assertDoesNotThrow(() -> {
            manager.addNewSubtask(hotelBooking);
        }, "Добавленные задачи пересекаются по времени");
    }

    @Test
    public void checkForNonOverlappingSubtasks2() {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now(), Duration.ofMinutes(30));
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку", LocalDateTime.now().minusMinutes(20), Duration.ofMinutes(10));
        manager.addNewTask(snowRemoval);
        manager.addNewTask(waterTheFlowers);
        assertEquals(manager.getPrioritizedTasks().size(), 2, "Могут быть добавлены задачи только без пересечения");
    }

    @Test
    void checkEpicUpdateMethod() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        vacationTrip.setDescription("На озера");
        manager.updateEpic(vacationTrip);

        assertEquals(vacationTrip.getDescription(), "На озера", "Описание должно измениться 'На озера'");
    }

    @Test
    void checkGetEpicSubtasksMethod() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);

        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip, LocalDateTime.now().minusMinutes(300), Duration.ofMinutes(50));
        manager.addNewSubtask(travelPlan);
        manager.addNewSubtask(hotelBooking);

        List<Subtask> epicSubtasks = manager.getEpicSubtasks(idVacationTrip);

        assertEquals(2, epicSubtasks.size(), "В списке должны быть 2 подзадачи");
        assertEquals(travelPlan, epicSubtasks.get(0), "Первая подзадача должна быть travelPlan");
        assertEquals(hotelBooking, epicSubtasks.get(1), "Вторая подзадача должна быть hotelBooking");
    }

    @Test
    void checkDeleteTaskMethod() {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now(), Duration.ofMinutes(30));
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку", LocalDateTime.now().minusMinutes(20), Duration.ofMinutes(10));
        final int idSnowRemoval = manager.addNewTask(snowRemoval);
        final int idWaterTheFlowers = manager.addNewTask(waterTheFlowers);

        manager.deleteTask(idSnowRemoval);

        assertThrows(NotFoundException.class, () -> manager.getTask(idSnowRemoval), "Задача snowRemoval должна быть удалена");
        assertNotNull(manager.getTask(idWaterTheFlowers), "Задача waterTheFlowers не должна быть удалена");
    }

    @Test
    void checkDeleteEpicMethod() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        Epic choosingPpuppy = new Epic("Завести собаку", "Должна быть комнатная порода", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        final int idChoosingPpuppy = manager.addNewEpic(choosingPpuppy);

        manager.deleteEpic(idChoosingPpuppy);

        assertThrows(NotFoundException.class, () -> manager.getEpic(idChoosingPpuppy), "Задача choosingPpuppy должна быть удалена");
        assertNotNull(manager.getEpic(idVacationTrip), "Задача VacationTrip не должна быть удалена");
    }

    @Test
    void checkDeleteSubtasksMethod() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip, LocalDateTime.now().minusMinutes(300), Duration.ofMinutes(50));
        manager.addNewSubtask(travelPlan);
        manager.addNewSubtask(hotelBooking);

        manager.deleteSubtasks();

        List<Subtask> subtasks = manager.getSubtasks();
        assertTrue(subtasks.isEmpty(), "Подзадач не должно быть");
    }


    @Test
    void checkGetPrioritizedTasksMethod() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip, LocalDateTime.now().minusMinutes(300), Duration.ofMinutes(50));
        manager.addNewSubtask(travelPlan);
        manager.addNewSubtask(hotelBooking);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        assertFalse(prioritizedTasks.isEmpty(), "Список приоритетных задач должен быть заполненным");

        assertFalse(hotelBooking.getStartTime().isBefore(travelPlan.getStartTime()), "Время начала задачи travelPlan должно быть раньше hotelBooking");
    }

    @Test
    void checkUpdateTimeForEpicMethod() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip, LocalDateTime.now().minusMinutes(300), Duration.ofMinutes(50));
        manager.addNewSubtask(travelPlan);
        manager.addNewSubtask(hotelBooking);

        Epic updatedEpic = manager.getEpic(idVacationTrip);

        LocalDateTime updatedStartTime = travelPlan.getStartTime();
        assertEquals(updatedStartTime, updatedEpic.getStartTime(), "Стартовое время у ранней подзадачи и эпика должно быть одинаковое");

        Duration updateDuration = travelPlan.getDuration().plus(hotelBooking.getDuration());
        assertEquals(updateDuration, updatedEpic.getDuration(), "Продолжительность эпика должна быть рассчитана по времени от начала самой ранней подзадачи + продолжительность всех задач");
    }
}