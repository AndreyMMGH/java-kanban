package http;

import com.google.gson.*;

import http.handler.TaskListTypeToken;
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


public class HistoryHandlerTest {

    private TasksManager manager;
    private HttpTaskServer server;
    private Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTasksManager();
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void shouldGetHistory() throws IOException, InterruptedException {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now().minusMinutes(500), Duration.ofMinutes(30));
        int idSnowRemoval = manager.addNewTask(snowRemoval);
        manager.getTask(idSnowRemoval);
        Epic vacationTrip = new Epic("Съездить в отпуск", "В горную местность", null, null, null);
        int idVacationTrip = manager.addNewEpic(vacationTrip);
        manager.getEpic(idVacationTrip);
        Subtask travelPlan = new Subtask("Составить план поездки", "Выбрать регион и туристические маршруты", idVacationTrip, LocalDateTime.now().minusMinutes(440), Duration.ofMinutes(120));
        int idTravelPlan = manager.addNewSubtask(travelPlan);
        manager.getSubtask(idTravelPlan);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> answerToTheTask = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");
        assertEquals(3, answerToTheTask.size(), "В результате должно быть 2 подзадачи");
        assertNotNull(answerToTheTask, "История не должна быть пустой.");
    }
}