package http;

import com.google.gson.*;

import com.google.gson.reflect.TypeToken;
import http.handler.BaseHttpHandler;
import model.Status;
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


public class TaskHandlerTest {

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
    public void shouldGetTasks() throws IOException, InterruptedException {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now().minusMinutes(500), Duration.ofMinutes(30));
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку", LocalDateTime.now().minusMinutes(470), Duration.ofMinutes(10));
        manager.addNewTask(snowRemoval);
        manager.addNewTask(waterTheFlowers);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> answerToTheTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");
        assertEquals(2, answerToTheTasks.size(), "В результате должно быть 2 задачи");
    }

    @Test
    public void shouldGetIdTask() throws IOException, InterruptedException {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now().minusMinutes(500), Duration.ofMinutes(30));
        manager.addNewTask(snowRemoval);

        String idSnowRemoval = gson.toJson(snowRemoval.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?idSnowRemoval=" + idSnowRemoval);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> answerToTheTask = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(snowRemoval, answerToTheTask.getFirst(), "Задачи разные");
    }

    @Test
    public void shouldGetIdTask404() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldAddTask() throws IOException, InterruptedException {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now().minusMinutes(500), Duration.ofMinutes(30));
        String taskJson = gson.toJson(snowRemoval);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Почистить снег", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldAddTask406() throws IOException, InterruptedException {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now().minusMinutes(500), Duration.ofMinutes(30));
        manager.addNewTask(snowRemoval);
        Task waterTheFlowers = new Task("Полить цветы", "Для полива использовать лейку", LocalDateTime.now().minusMinutes(500), Duration.ofMinutes(40));
        String taskJson = gson.toJson(waterTheFlowers);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void shouldUpdateTask() throws IOException, InterruptedException {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now().minusMinutes(500), Duration.ofMinutes(30));
        manager.addNewTask(snowRemoval);
        Task snowRemoval2 = new Task(1, "Почистить крыльцо", "Для чистки взять новую лопату", Status.NEW, LocalDateTime.now().minusMinutes(500), Duration.ofMinutes(30));

        String taskJson = gson.toJson(snowRemoval2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Из-за пересечения задача должна быть одна");
        assertEquals("Почистить крыльцо", tasksFromManager.getFirst().getName(), "Наименование должно быть новым");
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        Task snowRemoval = new Task("Почистить снег", "Для чистки взять новую лопату", LocalDateTime.now().minusMinutes(500), Duration.ofMinutes(30));
        int idSnowRemoval = manager.addNewTask(snowRemoval);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + snowRemoval.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");
        assertEquals(0, manager.getTasks().size(), "Задач не должно быть");
    }
}