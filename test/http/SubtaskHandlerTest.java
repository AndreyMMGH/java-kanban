package http;

import com.google.gson.*;

import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Task;
import service.InMemoryTasksManager;
import service.TasksManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class SubtaskHandlerTest {

    private TasksManager manager;
    private HttpTaskServer server;
    private Gson gson = HttpTaskServer.getGson();


    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTasksManager();
        server = new HttpTaskServer(manager);
        manager.deleteSubtasks();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void shouldGetSubtasks() throws IOException, InterruptedException {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip, LocalDateTime.now().minusMinutes(300), Duration.ofMinutes(50));
        manager.addNewSubtask(travelPlan);
        manager.addNewSubtask(hotelBooking);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> answerToTheSubtasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");
        assertEquals(2, answerToTheSubtasks.size(), "В результате должно быть 2 подзадачи");
    }

    @Test
    public void shouldGetIdSubtask() throws IOException, InterruptedException {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        manager.addNewSubtask(travelPlan);

        String idTravelPlan = gson.toJson(travelPlan.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks?idTravelPlan=" + idTravelPlan);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> answerToTheSubtask = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(travelPlan, answerToTheSubtask.getFirst(), "Подзадачи разные");
    }

    @Test
    public void shouldGetIdSubtask404() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldAddSubtask() throws IOException, InterruptedException {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));

        String taskJson = gson.toJson(travelPlan);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Составить план поездки", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldAddTask406() throws IOException, InterruptedException {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        manager.addNewSubtask(travelPlan);
        Subtask hotelBooking = new Subtask("Забронировать жилье", "Посмотреть гостевые дома и квартиры", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(50));

        String taskJson = gson.toJson(hotelBooking);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Должно произойти пересечение по времени подзадач");

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
    }

    @Test
    public void shouldUpdateTask() throws IOException, InterruptedException {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        manager.addNewSubtask(travelPlan);
        Subtask travelPlan2 = new Subtask(2, "Составить план поездки 2", "Выбрать регион и туристические маршруты", Status.NEW, idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));

        String taskJson = gson.toJson(travelPlan2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Из-за пересечения подзадача должна быть одна");
        assertEquals("Составить план поездки 2", subtasksFromManager.getFirst().getName(), "Наименование должно быть новым");
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        manager.addNewSubtask(travelPlan);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + travelPlan.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");
        assertEquals(0, manager.getSubtasks().size(), "Подзадач не должно быть");
    }
}