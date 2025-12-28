import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void equalsIdTask () {
        Task task1 = new Task("Сделать уроки", "Написать сочинение");
        task1.setId(2);
        Task task2 = new Task("Сделать уроки", "Написать сочинение");
        task2.setId(2);
        assertEquals(task1, task2);
    }

    @Test
    void equalsIdEpicAndSubTask () {
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        epicTask1.setId(1);
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", 1);
        subTask1.setId(1);
        assertEquals(epicTask1, subTask1);
    }


    @Test
    void checkConflictsBetweenId () {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Сделать уроки", "Написать сочинение");
        task1.setId(4);
        manager.createNewTask(task1);

        Task task2 = new Task("Сделать ремонт", "Поклеить обои");
        int task2Id = manager.createNewTask(task2);
        assertNotEquals(4, task2Id);


    }

    @Test
    void invariabilityOfTask () {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Сделать уроки", "Написать сочинение");
        Task copyTask = new Task("Сделать уроки", "Написать сочинение");
        manager.createNewTask(task1);
        assertEquals(copyTask.getName(), task1.getName());
        assertEquals(copyTask.getDescription(), task1.getDescription());
    }

}