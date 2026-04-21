import models.Epic;
import models.Subtask;
import models.Task;
import java.time.Duration;
import java.time.LocalDateTime;

public class TestData {
    public static final String NAME = "test1";
    public static final String DESCRIPTION = "testing1";
    public static final Duration DURATION = Duration.ofMinutes(15);
    public static final LocalDateTime START_TIME = LocalDateTime.of(2026, 10, 20, 15, 10);
    public static final int EPIC_ID = 1;

    public static Task newTask() {
        return new Task(NAME, DESCRIPTION, DURATION, START_TIME);
    }

    public static Task newStartTimeTask() {
        return new Task(NAME, DESCRIPTION, DURATION, START_TIME.plusHours(2));
    }

    public static Epic newEpic() {
        return new Epic(NAME, DESCRIPTION);
    }


    public static Subtask newSubTask(int epicId) {
        return new Subtask(NAME, DESCRIPTION, epicId, DURATION, START_TIME);
    }

    public static Subtask newStartTimeSubTask(int epicId) {
        return new Subtask(NAME, DESCRIPTION, epicId, DURATION, START_TIME.plusHours(1));
    }
}
