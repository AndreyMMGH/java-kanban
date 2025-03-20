package http.handler;

import com.sun.net.httpserver.HttpExchange;
import service.TasksManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
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
        text = gson.toJson(manager.getHistory());
        sendText(httpExchange, text, 200);
    }
}
