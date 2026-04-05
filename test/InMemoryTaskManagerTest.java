import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @Test
    void addDifferentTasksInInMemoryTaskManager() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(25), LocalDateTime.now().plusHours(3));
        int taskId1 = manager.createNewTask(task1);

        Task task2 = new Task("Сделать ремонт", "Поклеить обои", Duration.ofMinutes(100), LocalDateTime.now().plusHours(1));
        int taskId2 = manager.createNewTask(task2);

        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        int epicId1 = manager.createNewEpic(epicTask1);

        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId1, Duration.ofMinutes(20), LocalDateTime.now().plusHours(4));
        manager.createNewSubTask(subTask1);
        Subtask subTask2 = new Subtask("Купить новогодние подарки", "5 подарков", epicId1, Duration.ofMinutes(45), LocalDateTime.now().plusHours(5));
        manager.createNewSubTask(subTask2);

        assertNotNull(manager.getTaskById(1));
        assertNotNull(manager.getTaskById(3));
    }

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
}
