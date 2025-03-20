package http.handler;

import com.sun.net.httpserver.HttpExchange;
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
        if (path.length == 2 && path[1].equals("tasks")) {
            text = gson.toJson(manager.getTasks());
            sendText(httpExchange, text, 200);
        } else {
            try {
                int idTask = Integer.parseInt(path[2]);
                Task task = manager.getTask(idTask);
                if (task == null) {
                    sendNotFound(httpExchange);
                } else {
                    text = gson.toJson(task);
                    sendText(httpExchange, text, 200);
                }
            } catch (NumberFormatException e) {
                sendNotFound(httpExchange);
            }
        }
    }

    private void handlePostTask(HttpExchange httpExchange) throws IOException {
        String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            Task task = gson.fromJson(body, Task.class);
            if (manager.getTask(task.getId()) == null) {
                manager.addNewTask(task);
                sendText(httpExchange, "Задача создана", 201);
            } else {
                manager.updateTask(task);
                sendText(httpExchange, "Задача обновлена", 201);
            }
        } catch (TaskValidationException e) {
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
