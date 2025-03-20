package http.handler;

import com.sun.net.httpserver.HttpExchange;
import http.exception.NotFoundException;
import model.Task;
import service.TaskValidationException;
import service.TasksManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TasksManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] pathTask = httpExchange.getRequestURI().getPath().split("/");

        switch (method) {
            case "GET":
                handleGetTask(httpExchange, pathTask);
                break;
            case "POST":
                handlePostTask(httpExchange);
                break;
            case "DELETE":
                handleDeleteTask(httpExchange, pathTask);
                break;
            default:
                sendNotFound(httpExchange);
        }
    }

    private void handleGetTask(HttpExchange httpExchange, String[] path) throws IOException {
        try {
            if (path.length == 2 && path[1].equals("tasks")) {
                text = gson.toJson(manager.getTasks());
                sendText(httpExchange, text, 200);
            } else {
                int idTask = Integer.parseInt(path[2]);
                Task task = manager.getTask(idTask);
                text = gson.toJson(task);
                sendText(httpExchange, text, 200);
            }
        } catch (NumberFormatException | NotFoundException e) {
            sendNotFound(httpExchange);
        }
    }

    private void handlePostTask(HttpExchange httpExchange) throws IOException {
        try {
            String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);
            try {
                manager.getTask(task.getId());
                manager.updateTask(task);
                sendText(httpExchange, "Задача обновлена", 201);
            } catch (NotFoundException e) {
                manager.addNewTask(task);
                sendText(httpExchange, "Задача создана", 201);
            }
        } catch (Exception e) {
            sendHasInteractions(httpExchange);
        }
    }

    private void handleDeleteTask(HttpExchange httpExchange, String[] path) throws IOException {
        try {
            int idTask = Integer.parseInt(path[2]);
            manager.deleteTask(idTask);
            sendText(httpExchange, "Задача удалена", 200);
        } catch (NumberFormatException e) {
            sendNotFound(httpExchange);
        }
    }
}
