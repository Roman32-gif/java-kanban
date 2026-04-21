package models;
import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.epicId = epicId;

    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "models.Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public Subtask copy() {
        Subtask copy = new Subtask(this.getName(), this.getDescription(), this.getEpicId(), this.getDuration(), this.getStartTime());
        copy.setId(this.getId());
        copy.setStatus(this.getStatus());
        return copy;
    }
}
