package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import http.adapter.DurationTypeAdapter;
import http.adapter.LocalDateTimeAdapter;
import http.handler.PrioritizedHandler;
import http.handler.EpicHandler;
import http.handler.HistoryHandler;
import http.handler.SubtaskHandler;
import http.handler.TaskHandler;
import service.InMemoryTasksManager;
import service.TasksManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TasksManager manager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(manager));
        httpServer.createContext("/epics", new EpicHandler(manager));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public static void main(String[] args) throws IOException {
        InMemoryTasksManager manager = new InMemoryTasksManager();
        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }

    public void start() {
        System.out.println("Сервер запущен!");
        httpServer.start();
    }

    public void stop() {
        System.out.println("Сервер остановлен!");
        httpServer.stop(0);
    }

    public static Gson getGson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .setPrettyPrinting()
                .create();
        return gson;
    }

}
