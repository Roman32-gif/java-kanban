import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryTaskManagerTest {
    @Test
    void addDifferentTasksInInMemoryTaskManager() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Сделать уроки", "Написать сочинение");
        int taskId1 = manager.createNewTask(task1);

        Task task2 = new Task("Сделать ремонт", "Поклеить обои");
        int taskId2 = manager.createNewTask(task2);

        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        int epicId1 = manager.createNewEpic(epicTask1);

        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId1);
        manager.createNewSubTask(subTask1);
        Subtask subTask2 = new Subtask("Купить новогодние подарки", "5 подарков", epicId1);
        manager.createNewSubTask(subTask2);

        assertNotNull(manager.getTaskById(1));
        assertNotNull(manager.getTaskById(3));
    }
}
