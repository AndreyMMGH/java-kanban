package http.handler;

import service.TasksManager;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TasksManager manager) {
        super(manager);
    }
}
