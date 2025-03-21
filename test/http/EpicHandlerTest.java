package http;

import com.google.gson.*;

import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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


public class EpicHandlerTest {

    private TasksManager manager;
    private HttpTaskServer server;
    private Gson gson = HttpTaskServer.getGson();


    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTasksManager();
        server = new HttpTaskServer(manager);
        manager.deleteTasks();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void shouldGetEpics() throws IOException, InterruptedException {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        Epic choosingPpuppy = new Epic("Завести собаку", "Должна быть комнатная порода", null, null, null);
        manager.addNewEpic(vacationTrip);
        manager.addNewEpic(choosingPpuppy);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> answerToTheEpics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");
        assertEquals(2, answerToTheEpics.size(), "В результате должно быть 2 эпика");
    }

    @Test
    public void shouldGetIdEpic() throws IOException, InterruptedException {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        manager.addNewEpic(vacationTrip);

        String idVacationTrip = gson.toJson(vacationTrip.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics?idVacationTrip=" + idVacationTrip);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> answerToTheEpic = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(vacationTrip, answerToTheEpic.getFirst(), "Эпики разные");
        assertEquals(vacationTrip.getName(), answerToTheEpic.getFirst().getName(), "Разные имена");
    }

    @Test
    public void shouldGetIdEpic404() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldAddEpic() throws IOException, InterruptedException {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        String taskJson = gson.toJson(vacationTrip);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Съездить в отпуск", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");
    }


    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        manager.addNewEpic(vacationTrip);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + vacationTrip.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");
        assertEquals(0, manager.getEpics().size(), "Эпиков не должно быть");
    }

    @Test
    public void shouldGetSubtasksForEpic() throws IOException, InterruptedException {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        int idTravelPlan = manager.addNewSubtask(travelPlan);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + idVacationTrip + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> answerForSubtaskEpic = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(1, answerForSubtaskEpic.size(), "Количество подзадач у эпика должно быть равно 1");
    }

    @Test
    public void shouldGetSubtasksForEpic404() throws IOException, InterruptedException {
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        final int idVacationTrip = manager.addNewEpic(vacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        manager.addNewSubtask(travelPlan);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + 3 + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}