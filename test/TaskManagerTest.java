import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class TaskManagerTest <T extends TaskManager> {
    protected  T manager;
    protected abstract T createManager();

    @BeforeEach
    void setUp () {
        manager = createManager();
    }

    @Test
    void epicExistsForSubtask() {
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        int epicId = manager.createNewEpic(epicTask1);
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createNewSubTask(subTask1);
        int id = subTask1.getEpicId();
        assertEquals(epicId, id);
    }

    @Test
    void taskTimeSet() {
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(60), LocalDateTime.of(2026, 5, 10, 20, 0));
        task1.setId(2);
        manager.createNewTask(task1);
        Task task2 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(60), LocalDateTime.of(2026, 5, 10, 20, 30));
        task2.setId(2);
        assertThrows(RuntimeException.class, () -> {
            manager.createNewTask(task2);
        } );
    }
}
