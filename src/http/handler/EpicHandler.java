package http.handler;

import com.sun.net.httpserver.HttpHandler;
import http.exception.NotFoundException;
import model.Subtask;
import model.Epic;
import com.sun.net.httpserver.HttpExchange;
import service.TasksManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHandler(TasksManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] pathEpic = httpExchange.getRequestURI().getPath().split("/");

        switch (method) {
            case "GET":
                handleGetEpic(httpExchange, pathEpic);
                break;
            case "POST":
                handlePostEpic(httpExchange);
                break;
            case "DELETE":
                handleDeleteEpic(httpExchange, pathEpic);
                break;
            default:
                sendNotFound(httpExchange);
        }
    }

    private void handleGetEpic(HttpExchange httpExchange, String[] path) throws IOException {
        try {
            if (path.length == 2 && path[1].equals("epics")) {
                sendText(httpExchange, gson.toJson(manager.getEpics()), 200);
            } else if (path.length == 3 && path[1].equals("epics")) {
                int idEpic = Integer.parseInt(path[2]);
                Epic epic = manager.getEpic(idEpic);
                sendText(httpExchange, gson.toJson(epic), 200);
            } else if (path.length == 4 && path[3].equals("subtasks")) {
                int idEpic = Integer.parseInt(path[2]);
                List<Subtask> subtasksIds = manager.getEpicSubtasks(idEpic);
                if (!subtasksIds.isEmpty()) {
                    sendText(httpExchange, gson.toJson(subtasksIds), 200);
                } else {
                    sendNotFound(httpExchange);
                }
            }
        } catch (NotFoundException | NumberFormatException e) {
            sendNotFound(httpExchange);
        }
    }

    private void handlePostEpic(HttpExchange httpExchange) throws IOException {

        try {
            String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(body, Epic.class);
            try {
                manager.addNewEpic(epic);
                sendText(httpExchange, "Эпик добавлен", 201);
            } catch (NotFoundException e) {
                sendNotFound(httpExchange);
            }
        } catch (NullPointerException exp) {
            sendText(httpExchange, "Некорректный запрос", 400);
        }
    }

    private void handleDeleteEpic(HttpExchange httpExchange, String[] path) throws IOException {
        try {
            int idEpic = Integer.parseInt(path[2]);
            manager.deleteEpic(idEpic);
            sendText(httpExchange, "Эпик удален", 200);
        } catch (Exception e) {
            sendNotFound(httpExchange);
        }
    }

}
