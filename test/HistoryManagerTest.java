import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import models.Status;
import models.Task;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerTest {

    @Test
    void checkSavePreviousTask() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(30), LocalDateTime.now());
        int id = manager.createNewTask(task1);
        manager.getTask(id);
        List<Task> historyTasks = ((InMemoryTaskManager) manager).getHistory();
        assertEquals(Status.NEW, historyTasks.get(0).getStatus());

    }
}
