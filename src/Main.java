import model.Task;
import model.Subtask;
import model.Epic;
import model.Status;
//import service.FileBackedTaskManager;
import service.Managers;
import service.TasksManager;

import java.time.Duration;
import java.time.LocalDateTime;

//import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        //File file = new File("./resources/task.csv");
        //FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        TasksManager manager = Managers.getDefault();

        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now().minusMinutes(500), Duration.ofMinutes(30));
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку", LocalDateTime.now().minusMinutes(470), Duration.ofMinutes(10));
        final int idSnowRemoval = manager.addNewTask(snowRemoval);
        final int idWaterTheFlowers = manager.addNewTask(waterTheFlowers);

        Task beforeUpdateSnowRemoval = new Task(idSnowRemoval, "Почистить снег", "Для чистки взять новую лопату", Status.IN_PROGRESS, LocalDateTime.now().minusMinutes(460), Duration.ofMinutes(20));
        manager.updateTask(beforeUpdateSnowRemoval);

        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        Epic choosingPpuppy = new Epic("Завести собаку", "Должна быть комнатная порода", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        final int idChoosingPpuppy = manager.addNewEpic(choosingPpuppy);

        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip, LocalDateTime.now().minusMinutes(300), Duration.ofMinutes(50));
        Subtask breedSelection = new Subtask("Выбрать породу", "Можно длинношерстную", idChoosingPpuppy, LocalDateTime.now().minusMinutes(210), Duration.ofMinutes(200));
        final int idTravelPlan = manager.addNewSubtask(travelPlan);
        final int idHotelBooking = manager.addNewSubtask(hotelBooking);
        final int idBreedSelection = manager.addNewSubtask(breedSelection);

        hotelBooking.setStatus(Status.DONE);
        manager.updateSubtask(hotelBooking);

        System.out.println("Список задач:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("Список эпиков:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task epicSubtask : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("   " + epicSubtask);
            }
        }

        System.out.println("Список подзадач:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        manager.getEpic(3);
        manager.getEpic(4);
        manager.getTask(1);
        manager.getTask(2);
        manager.getTask(1);
        manager.getSubtask(5);
        manager.getSubtask(6);
        manager.getSubtask(6);
        manager.getSubtask(7);
        manager.getTask(2);
        manager.getSubtask(5);
        manager.getTask(1);
        manager.getEpic(4);

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}

