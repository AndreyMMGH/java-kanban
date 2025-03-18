package http.handler;

import service.TasksManager;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TasksManager manager) {
        super(manager);
    }
}
