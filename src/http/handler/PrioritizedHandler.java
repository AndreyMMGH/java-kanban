package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TasksManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
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
        sendText(httpExchange, gson.toJson(manager.getPrioritizedTasks()), 200);
    }
}
