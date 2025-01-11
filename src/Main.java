import model.Task;
import model.Subtask;
import model.Epic;
import model.Status;
import service.Managers;
import service.TasksManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TasksManager manager = Managers.getDefault();

        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату");
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку");
        final int idSnowRemoval = manager.addNewTask(snowRemoval);
        final int idWaterTheFlowers = manager.addNewTask(waterTheFlowers);

        Task beforeUpdateSnowRemoval = new Task(idSnowRemoval, "Почистить снег", "Для чистки взять новую лопату", Status.IN_PROGRESS);
        manager.updateTask(beforeUpdateSnowRemoval);

        Epic vacationTrip = new Epic("Съездить в отпуск", "Туда, где горы");
        Epic choosingPpuppy = new Epic("Завести собаку", "Должна быть комнатная порода");
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        final int idChoosingPpuppy = manager.addNewEpic(choosingPpuppy);

        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip);
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip);
        Subtask breedSelection = new Subtask("Выбрать породу", "Можно длинношерстную", idChoosingPpuppy);
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

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}

