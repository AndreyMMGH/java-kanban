package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

import java.util.List;


public class FileBackedTaskManager extends InMemoryTasksManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (file == null) {
            throw new ManagerSaveException("Данные из файла загрузить невозможно");
        }

        final FileBackedTaskManager managerForRecovery = new FileBackedTaskManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine();
            String line;

            managerForRecovery.generatorId = 0;
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                Task task = CSVTaskFormat.taskFromString(line);

                if (task != null) {
                    if (task.getId() > managerForRecovery.generatorId) {
                        managerForRecovery.generatorId = task.getId() + 1;
                    }

                    if (task.getTaskType() == TaskType.TASK) {
                        managerForRecovery.tasks.put(task.getId(), task);
                    }

                    if (task.getTaskType() == TaskType.EPIC) {
                        managerForRecovery.epics.put(task.getId(), (Epic) task);
                    }

                    if (task.getTaskType() == TaskType.SUBTASK) {
                        managerForRecovery.subtasks.put(task.getId(), (Subtask) task);
                    }
                }
            }

            for (Subtask subtask : managerForRecovery.subtasks.values()) {
                Epic epic = managerForRecovery.epics.get(subtask.getEpicId());
                epic.getSubtaskIds().add(subtask.getId());
            }

            String idsHistoryLine = bufferedReader.readLine();
            if (idsHistoryLine != null && !idsHistoryLine.isEmpty()) {
                List<Integer> IdsHistory = CSVTaskFormat.fromStringToHistoryId(idsHistoryLine);
                for (Integer taskId : IdsHistory) {
                    Task task = managerForRecovery.getTask(taskId);
                    Epic epic = managerForRecovery.getEpic(taskId);
                    Subtask subtask = managerForRecovery.getSubtask(taskId);
                    if (task != null || epic != null || subtask != null) {
                        managerForRecovery.historyManager.addTask(task);
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("При загрузке данных из файла произошла ошибка");
        }
        return managerForRecovery;
    }

    protected void save() throws ManagerSaveException {

        if (file == null) {
            throw new ManagerSaveException("Данные в файл не сохранены");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : getTasks()) {
                writer.write(CSVTaskFormat.toString(task));
                getTask(task.getId());
                writer.newLine();
            }

            for (Epic epic : getEpics()) {
                getEpic(epic.getId());
                writer.write(CSVTaskFormat.toString(epic));
                writer.newLine();
            }

            for (Subtask subtask : getSubtasks()) {
                getSubtask(subtask.getId());
                writer.write(CSVTaskFormat.toString(subtask));
                writer.newLine();
            }

            writer.newLine();


            writer.write(CSVTaskFormat.fromHistoryIdToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("При сохранении данных в файл произошла ошибка");
        }
    }

    @Override
    public int addNewTask(Task task) {
        super.addNewTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

}
