package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTasksManager implements TasksManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    protected int generatorId = 1;

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int addNewTask(Task task) {
        final int id = generatorId++;
        task.setId(id);
        if (checkingForTaskIntersection(task)) {
            tasks.put(id, task);
            prioritizedTasks.add(task);
            return id;
        } else {
            throw new TaskValidationException("Новая задача пересекается по времени с существующей");
        }
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
        if (checkingForTaskIntersection(subtask)) {
            epic.addSubtaskId(id);
            subtasks.put(id, subtask);
            prioritizedTasks.add(subtask);
            updateEpicStat(epic);
            if (subtask.getStartTime() != null) {
                updateTimeForEpic(epic);
            }
            return id;
        } else {
            throw new TaskValidationException("Новая подзадача пересекается по времени с существующей");
        }
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
        if (checkingForTaskIntersection(task)) {
            tasks.put(taskId, task);
            prioritizedTasks.add(task);
        } else {
            throw new TaskValidationException("Обновление невозможно. Новая задача пересекается по времени с существующей");
        }
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
        updateTimeForEpic(epic);
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
        updateTimeForEpic(epic);
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
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }

        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(getTask(id));
    }

    @Override
    public void deleteEpic(int id) {
        ArrayList<Integer> epicSubtaskIds = epics.get(id).getSubtaskIds();
        epics.remove(id);
        historyManager.remove(id);
        for (Integer epicSubtaskId : epicSubtaskIds) {
            subtasks.remove(epicSubtaskId);
            historyManager.remove(epicSubtaskId);
            prioritizedTasks.remove(getSubtask(id));
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
        prioritizedTasks.remove(getSubtask(id));
        updateEpicStat(epic);
        updateTimeForEpic(epic);
    }

    @Override
    public void deleteTasks() {
        List<Task> tasksDelete = new ArrayList<>(tasks.values());

        for (Task task : tasksDelete) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
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
            prioritizedTasks.remove(subtask);
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
            prioritizedTasks.remove(subtask);
        }

        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void updateTimeForEpic(Epic epic) {
        List<Subtask> subtasks = getEpicSubtasks(epic.getId());
        if (subtasks.isEmpty()) {
            return;
        }

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        for (Subtask subtask : subtasks) {
            LocalDateTime subtaskStartTime = subtask.getStartTime();
            LocalDateTime subtaskEndTime = subtask.getEndTime();

            if (subtaskStartTime == null || subtaskEndTime == null) {
                continue;
            }

            if (startTime == null || subtaskStartTime.isBefore(startTime)) {
                startTime = subtaskStartTime;
            }
            if (endTime == null || subtaskEndTime.isAfter(endTime)) {
                endTime = subtaskEndTime;
            }
        }

        if (startTime != null && endTime != null) {
            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            epic.setDuration(Duration.between(startTime, endTime));
        }
    }

    public boolean checkingForTaskIntersection(Task task) {
        if (task == null || task.getStartTime() == null || task.getEndTime() == null) {
            return true;
        }

        return prioritizedTasks.stream()
                .filter(existingTask -> existingTask.getStartTime() != null && existingTask.getEndTime() != null)
                .noneMatch(existingTask ->
                        task.getStartTime().isBefore(existingTask.getEndTime()) &&
                                task.getEndTime().isAfter(existingTask.getStartTime()));
    }

}
