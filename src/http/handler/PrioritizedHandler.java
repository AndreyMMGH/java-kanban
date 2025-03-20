package http.handler;

import com.sun.net.httpserver.HttpExchange;
import service.TasksManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TasksManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetPrioritized(httpExchange);
                break;
            default:
                sendNotFound(httpExchange);
        }
    }

    private void handleGetPrioritized(HttpExchange httpExchange) throws IOException {
        text = gson.toJson(manager.getPrioritizedTasks());
        sendText(httpExchange, text, 200);
    }
}
