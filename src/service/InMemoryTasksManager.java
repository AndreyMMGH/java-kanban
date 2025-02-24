package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTasksManager implements TasksManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    protected int generatorId = 1;

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int addNewTask(Task task) {
        final int id = generatorId++;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = generatorId++;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int epicSubtaskId = subtask.getEpicId();
        if (!epics.containsKey(epicSubtaskId)) {
            System.out.println("Эпик не найден!");
            return -1;
        }
        final int id = generatorId++;
        subtask.setId(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(id);
        subtasks.put(id, subtask);
        updateEpicStat(epic);
        return id;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.addTask(task);
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.addTask(subtask);
        return subtasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.addTask(epic);
        return epics.get(id);
    }

    @Override
    public void updateTask(Task task) {
        final int taskId = task.getId();
        if (!tasks.containsKey(taskId)) {
            return;
        }
        tasks.put(taskId, task);
    }

    @Override
    public void updateEpic(Epic epic) {
        final int epicId = epic.getId();
        if (!epics.containsKey(epicId)) {
            return;
        }
        Epic savedEpic = epics.get(epicId);
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Integer subtaskId = subtask.getId();
        Subtask savedSubtask = subtasks.get(subtaskId);
        if (savedSubtask == null || !subtasks.containsKey(subtaskId)) {
            return;
        }
        int epicId = subtask.getEpicId();
        subtasks.replace(subtaskId, subtask);
        Epic epic = epics.get(epicId);
        updateEpicStat(epic);
    }

    private void updateEpicStat(Epic epic) {
        int allIsDoneCount = 0;
        int allIsInNewCount = 0;
        ArrayList<Integer> idList = epic.getSubtaskIds();

        for (Integer id : idList) {
            Subtask subtask = subtasks.get(id);
            if (subtask.getStatus() == Status.DONE) {
                allIsDoneCount++;
            }
            if (subtask.getStatus() == Status.NEW) {
                allIsInNewCount++;
            }
        }

        if (allIsInNewCount == idList.size()) {
            epic.setStatus(Status.NEW);
        } else if (allIsDoneCount == idList.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }


    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return epicSubtasks;
        }
        for (int id : epic.getSubtaskIds()) {
            epicSubtasks.add(subtasks.get(id));
        }
        return epicSubtasks;
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        ArrayList<Integer> epicSubtaskIds = epics.get(id).getSubtaskIds();
        epics.remove(id);
        historyManager.remove(id);
        for (Integer epicSubtaskId : epicSubtaskIds) {
            subtasks.remove(epicSubtaskId);
            historyManager.remove(epicSubtaskId);
        }
    }

    @Override
    public void deleteSubTask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        historyManager.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStat(epic);
    }

    @Override
    public void deleteTasks() {
        List<Task> tasksDelete = new ArrayList<>(tasks.values());

        for (Task task : tasksDelete) {
            historyManager.remove(task.getId());
        }

        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        List<Epic> epicsDelete = new ArrayList<>(epics.values());
        List<Subtask> subtasksDelete = new ArrayList<>(subtasks.values());

        for (Epic epic : epicsDelete) {
            historyManager.remove(epic.getId());
        }

        for (Subtask subtask : subtasksDelete) {
            historyManager.remove(subtask.getId());
        }

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        List<Subtask> subtasksDelete = new ArrayList<>(subtasks.values());

        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStat(epic);
        }

        for (Subtask subtask : subtasksDelete) {
            historyManager.remove(subtask.getId());
        }

        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
