package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int maxNumberOfRecords = 10;
    private final List<Task> historyList = new ArrayList<>();

    @Override
    public void addTask(Task task) {
        if (historyList.size() == maxNumberOfRecords) {
            historyList.removeFirst();
        }
        historyList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

}
