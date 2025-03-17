package http;

import com.sun.net.httpserver.HttpServer;
import  http.handler.PrioritizedHandler;
import http.handler.EpicHandler;
import http.handler.HistoryHandler;
import http.handler.SubtaskHandler;
import http.handler.TaskHandler;
import model.Task;
import service.TasksManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TasksManager manager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.createContext("/epics", new EpicHandler());
        httpServer.createContext("/subtasks", new SubtaskHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/tasks", new PrioritizedHandler());
    }


    public static void main(String[] args) throws IOException {

    }

    public void start() {
        System.out.println("Старт ");
    }

}
