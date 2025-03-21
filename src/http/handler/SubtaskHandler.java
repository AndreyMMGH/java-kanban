package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.exception.NotFoundException;
import model.Subtask;
import service.TaskValidationException;
import service.TasksManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    public SubtaskHandler(TasksManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] pathSubtask = httpExchange.getRequestURI().getPath().split("/");

        switch (method) {
            case "GET":
                handleGetSubtask(httpExchange, pathSubtask);
                break;
            case "POST":
                handlePostSubtask(httpExchange);
                break;
            case "DELETE":
                handleDeleteSubtask(httpExchange, pathSubtask);
                break;
            default:
                sendNotFound(httpExchange);
        }
    }

    private void handleGetSubtask(HttpExchange httpExchange, String[] path) throws IOException {
        try {
            if (path.length == 2 && path[1].equals("subtasks")) {
                sendText(httpExchange, gson.toJson(manager.getSubtasks()), 200);
            } else {
                int idSubtask = Integer.parseInt(path[2]);
                Subtask subtask = manager.getSubtask(idSubtask);
                sendText(httpExchange, gson.toJson(subtask), 200);
            }
        } catch (NumberFormatException | NotFoundException e) {
            sendNotFound(httpExchange);
        }
    }

    private void handlePostSubtask(HttpExchange httpExchange) throws IOException {
        try {
            String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);

            try {
                manager.getSubtask(subtask.getId());
                manager.updateSubtask(subtask);
                sendText(httpExchange, "Подзадача обновлена", 201);
            } catch (NotFoundException e) {
                try {
                    manager.addNewSubtask(subtask);
                    sendText(httpExchange, "Подзадача создана", 201);
                } catch (TaskValidationException t) {
                    sendHasInteractions(httpExchange);
                }
            }
        } catch (NullPointerException exp) {
            sendText(httpExchange, "Некорректный запрос", 400);
        }
    }

    private void handleDeleteSubtask(HttpExchange httpExchange, String[] path) throws IOException {
        try {
            int idSubtask = Integer.parseInt(path[2]);
            manager.deleteSubTask(idSubtask);
            sendText(httpExchange, "Подзадача удалена", 200);
        } catch (NumberFormatException e) {
            sendNotFound(httpExchange);
        }
    }
}
