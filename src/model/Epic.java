package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public final class Epic extends Task {

    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(id, name, description, status, startTime, duration);
        this.endTime = endTime;
    }

    public Epic(String name, String description, LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(name, description, startTime, duration);
        this.endTime = endTime;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void cleanSubtaskIds() {
        subtaskIds.clear();
    }

    public void removeSubtask(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name= " + name + '\'' +
                ", description = " + description + '\'' +
                ", id=" + id +
                ", subtaskIds=" + subtaskIds +
                ", status = " + status +
                ", startTime = " + startTime +
                ", duration = " + duration +
                ", endTime = " + endTime +
                '}';
    }
}

