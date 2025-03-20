package http.handler;

import model.Subtask;
import model.Epic;
import com.sun.net.httpserver.HttpExchange;
import service.TasksManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
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
        if (path.length == 2 && path[1].equals("epics")) {
            text = gson.toJson(manager.getEpics());
            sendText(httpExchange, text, 200);
        } else if (path.length == 3 && path[1].equals("epics")) {
            try {
                int idEpic = Integer.parseInt(path[2]);
                Epic epic = manager.getEpic(idEpic);
                if (epic == null) {
                    sendNotFound(httpExchange);
                } else {
                    text = gson.toJson(epic);
                    sendText(httpExchange, text, 200);
                }
            } catch (NumberFormatException e) {
                sendNotFound(httpExchange);
            }
        } else if (path.length == 4 && path[3].equals("subtasks")) {
            try {
                int idEpic = Integer.parseInt(path[2]);
                List<Subtask> subtasksIds = manager.getEpicSubtasks(idEpic);
                if (subtasksIds == null || subtasksIds.isEmpty()) {
                    sendNotFound(httpExchange);
                } else {
                    text = gson.toJson(subtasksIds);
                    sendText(httpExchange, text, 200);
                }
            } catch (NumberFormatException e) {
                sendNotFound(httpExchange);
            }
        }
    }

    private void handlePostEpic(HttpExchange httpExchange) throws IOException {
        String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            Epic epic = gson.fromJson(body, Epic.class);
            if (manager.getEpic(epic.getId()) == null) {
                manager.addNewEpic(epic);
                sendText(httpExchange, "Эпик добавлен", 201);
            } else {
                sendNotFound(httpExchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(httpExchange);
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
