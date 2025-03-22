package http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TasksManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TasksManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetHistory(httpExchange);
                break;
            default:
                sendNotFound(httpExchange);
        }
    }

    private void handleGetHistory(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, gson.toJson(manager.getHistory()), 200);
    }
}
