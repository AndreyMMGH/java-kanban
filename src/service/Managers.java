package service;

public class Managers {
    public static TasksManager getDefault() {

        return new InMemoryTasksManager();
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
}
