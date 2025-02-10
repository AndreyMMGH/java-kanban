package service;

import model.Task;

import java.util.List;

public interface HistoryManager {
    void addTask(Task task);

    void remove(int id);

    void removeAll();

    List<Task> getHistory();
}
