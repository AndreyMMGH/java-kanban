package http.handler;

import service.TasksManager;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TasksManager manager) {
        super(manager);
    }
}
