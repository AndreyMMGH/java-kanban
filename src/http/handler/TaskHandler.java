package http.handler;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TasksManager;

import java.io.IOException;

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
                handelGetTask(httpExchange, pathTask);
                break;
            case "POST":
                handelPostTask(httpExchange);
                break;
            case "DELETE":
                handelDeleteTask(httpExchange, pathTask);
                break;
            default:
                sendNotFound(httpExchange);
        }
    }

    private void handelGetTask(HttpExchange httpExchange, String[] path) throws IOException {
        if (path.length == 2) {
            text = gson.toJson(manager.getTasks());
            sendText(httpExchange, text, 200);
        } else {
            try {
                int id = Integer.parseInt(path[2]);
                Task task = manager.getTask(id);
                if (task != null) {
                    text = gson.toJson(task);
                    sendText(httpExchange, text, 200);
                } else {
                    sendNotFound(httpExchange);
                }
            } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                sendNotFound(httpExchange);
            }
        }
    }

    private void handelPostTask(HttpExchange httpExchange) throws IOException {

    }

    private void handelDeleteTask(HttpExchange httpExchange, String[] path) throws IOException {

    }
}
