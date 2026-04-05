package models;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();
    LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Duration.ZERO, null);
    }

    public void addSubtask(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }


    @Override
    public String toString() {
        return "models.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskId=" + subtaskIds +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public Epic copy() {
        Epic copy = new Epic(this.getName(), this.getDescription());
        copy.setId(this.getId());
        copy.setStatus(this.getStatus());
        copy.getSubtaskIds().addAll(this.getSubtaskIds());
        return copy;
    }
}
