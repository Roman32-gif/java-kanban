import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void equalsIdTask() {
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(15), LocalDateTime.now());
        task1.setId(2);
        Task task2 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(30), LocalDateTime.now());
        task2.setId(2);
        assertEquals(task1, task2);
    }

    @Test
    void equalsIdEpicAndSubTask() {
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        epicTask1.setId(1);
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", 1, Duration.ofMinutes(35), LocalDateTime.now());
        subTask1.setId(1);
        assertEquals(epicTask1, subTask1);
    }

    @Test
    void checkConflictsBetweenId() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(24), LocalDateTime.now());
        task1.setId(4);
        manager.createNewTask(task1);

        Task task2 = new Task("Сделать ремонт", "Поклеить обои", Duration.ofMinutes(46), LocalDateTime.now().plusHours(1));
        int task2Id = manager.createNewTask(task2);
        assertNotEquals(4, task2Id);
    }

    @Test
    void invariabilityOfTask() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(24), LocalDateTime.now());
        Task copyTask = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(40), LocalDateTime.now());
        manager.createNewTask(task1);
        assertEquals(copyTask.getName(), task1.getName());
        assertEquals(copyTask.getDescription(), task1.getDescription());
    }
}