package service;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;
import model.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class CSVTaskFormat {

    public static String toString(Task task) {
        if (!(task.getTaskType() == TaskType.SUBTASK)) {
            return task.getId() + "," +
                    task.getTaskType() + "," +
                    task.getName() + "," +
                    task.getStatus() + "," +
                    task.getDescription() + "," +
                    task.getStartTime() + "," +
                    task.getDuration() + "," +
                    task.getEndTime();
        } else {
            Subtask subtask = (Subtask) task;
            return subtask.getId() + "," +
                    subtask.getTaskType() + "," +
                    subtask.getName() + "," +
                    subtask.getStatus() + "," +
                    subtask.getDescription() + "," +
                    subtask.getEpicId() + "," +
                    subtask.getStartTime() + "," +
                    subtask.getDuration();
        }
    }

    public static Task taskFromString(String text) {
        final String[] texts = text.split(",");
        final int id = Integer.parseInt(texts[0]);
        final TaskType type = TaskType.valueOf(texts[1]);
        final String name = texts[2];
        final Status status = Status.valueOf(texts[3]);
        final String description = texts[4];
        final LocalDateTime startTime = LocalDateTime.parse(texts[5]);
        final Duration duration = Duration.parse(texts[6]);

        if (type == TaskType.TASK) {
            return new Task(id, name, description, status, startTime, duration);
        } else if (type == TaskType.EPIC) {
            final LocalDateTime endTime = LocalDateTime.parse(texts[7]);
            return new Epic(id, name, description, status, startTime, duration, endTime);
        } else if (type == TaskType.SUBTASK) {
            final int epicId = Integer.parseInt(texts[5]);
            return new Subtask(id, name, description, status, epicId, startTime, duration);
        }
        return null;
    }

    public static String fromHistoryIdToString(HistoryManager historyManager) {
        List<Task> idsHistory = historyManager.getHistory();

        if (idsHistory == null || idsHistory.isEmpty()) {
            return "";
        }

        StringBuilder idStringBuilder = new StringBuilder();
        for (int i = 0; i < idsHistory.size(); i++) {
            idStringBuilder.append(idsHistory.get(i).getId());

            if (i < idsHistory.size() - 1) {
                idStringBuilder.append(",");
            }
        }
        return idStringBuilder.toString();
    }

    public static List<Integer> fromStringToHistoryId(String text) {
        List<Integer> idsHistory = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        String[] idsText = text.split(",");
        for (String idString : idsText) {
            int id = Integer.parseInt(idString.trim());
            idsHistory.add(id);
        }
        return idsHistory;
    }

}




