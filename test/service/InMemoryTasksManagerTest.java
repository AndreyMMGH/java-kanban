package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTasksManagerTest {

    private TasksManager manager;

    @BeforeEach
    public void beforeEach() {

        manager = Managers.getDefault();
    }

    @Test
    public void epicCanAddItSelfAsSubtask() {
        Epic vacationTrip = new Epic(1, "Съездить в отпуск", "Туда, где горы", Status.NEW);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip);
        manager.addNewSubtask(travelPlan);
        assertFalse(vacationTrip.getSubtaskIds().contains(vacationTrip.getId()), "Эпик не может содержать себя в качестве подзадачи");
    }

    @Test
    public void subtaskIsNotEqualToItsEpic() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы");
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip);
        manager.addNewSubtask(travelPlan);
        assertNotEquals(idVacationTrip, travelPlan.getId(), "Подзадачу нельзя сделать своим эпиком");
    }

    @Test
    public void addTask() {
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку");
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
        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы");
        Epic choosingPpuppy = new Epic("Завести собаку", "Должна быть комнатная порода");
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
        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы");
        Epic choosingPpuppy = new Epic("Завести собаку", "Должна быть комнатная порода");
        int idVacationTrip = manager.addNewEpic(vacationTrip);
        int idChoosingPpuppy = manager.addNewEpic(choosingPpuppy);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip);
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip);
        Subtask breedSelection = new Subtask("Выбрать породу", "Можно длинношерстную", idChoosingPpuppy);
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
        Task snowRemoval = new Task(2, "Почистить снег", "Для чистки взять новую лопату", Status.NEW);
        manager.addNewTask(snowRemoval);
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку");
        manager.addNewTask(waterTheFlowers);
        assertNotEquals(snowRemoval.getId(), waterTheFlowers.getId(), "Произошел конфликт. Id одинаковые");
    }

    @Test
    public void checksTheImmutabilityOfTheTask() {
        Task waterTheFlowers = new Task(1, "Полить цветы", "Для полива использовать лейку", Status.NEW);
        manager.addNewTask(waterTheFlowers);
        Task recordedTask = manager.getTask(waterTheFlowers.getId());
        assertEquals(waterTheFlowers.getId(), recordedTask.getId(), "id не совпал");
        assertEquals(waterTheFlowers.getName(), recordedTask.getName(), "Имя не совпало");
        assertEquals(waterTheFlowers.getDescription(), recordedTask.getDescription(), "Описание не совпало");
        assertEquals(waterTheFlowers.getStatus(), recordedTask.getStatus(), "Статус не совпал");
    }

    @Test
    public void doNotSaveDeletedSubtaskInHistory() {
        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы");
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip);
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
        Epic vacationTrip = new Epic(1, "Съездить в отпуск", "Туда, где горы", Status.NEW);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip);
        int idTravelPlan = manager.addNewSubtask(travelPlan);
        manager.deleteSubTask(idTravelPlan);
        Epic updatedVacationTrip = manager.getEpic(idVacationTrip);
        assertFalse(updatedVacationTrip.getSubtaskIds().contains(idTravelPlan), "В списке подзадач эпика не должно быть id удаленной подзадачи");
    }

    @Test
    public void allowIdChange() {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату");
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
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату");
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку");
        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы");
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты",3);
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", 3);
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
}
