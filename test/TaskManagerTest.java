import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(24), LocalDateTime.now());
        task1.setId(4);
        manager.createNewTask(task1);

        Task task2 = new Task("Сделать ремонт", "Поклеить обои", Duration.ofMinutes(46), LocalDateTime.now().plusHours(1));
        int task2Id = manager.createNewTask(task2);
        assertNotEquals(4, task2Id);
    }

    @Test
    void invariabilityOfTask() {
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(24), LocalDateTime.now());
        Task copyTask = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(40), LocalDateTime.now());
        manager.createNewTask(task1);
        assertEquals(copyTask.getName(), task1.getName());
        assertEquals(copyTask.getDescription(), task1.getDescription());
    }

    @Test
    void epicNoAddLikeSubTask() {
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        int epicId = manager.createNewEpic(epicTask1);
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        subTask1.setId(epicId);
        int subTask1Id = manager.createNewSubTask(subTask1);
        assertEquals(-1, subTask1Id);
        Epic newEpic = manager.getEpic(epicId);
        assertTrue(newEpic.getSubtaskIds().isEmpty());
    }

    @Test
    void epicWithAllStatusesNewInSubTasks() {
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        int epicId = manager.createNewEpic(epicTask1);
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        subTask1.setStatus(Status.NEW);
        manager.createNewSubTask(subTask1);
        Subtask subTask2 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        subTask2.setStatus(Status.NEW);
        manager.createNewSubTask(subTask2);
        Subtask subTask3 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));
        subTask3.setStatus(Status.NEW);
        manager.createNewSubTask(subTask3);
        assertEquals(Status.NEW, epicTask1.getStatus());
    }

    @Test
    void epicWithAllStatusesDoneInSubTasks() {
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        int epicId = manager.createNewEpic(epicTask1);
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        subTask1.setStatus(Status.NEW);
        manager.createNewSubTask(subTask1);
        Subtask subTask2 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        subTask2.setStatus(Status.NEW);
        manager.createNewSubTask(subTask2);
        Subtask subTask3 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));
        subTask3.setStatus(Status.NEW);
        manager.createNewSubTask(subTask3);
        subTask1.setStatus(Status.DONE);
        manager.updateSubTask(subTask1);
        subTask2.setStatus(Status.DONE);
        manager.updateSubTask(subTask2);
        subTask3.setStatus(Status.DONE);
        manager.updateSubTask(subTask3);
        assertEquals(Status.DONE, epicTask1.getStatus());
    }

    @Test
    void epicWithDoneAndNewStatusesInSubTasks() {
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        int epicId = manager.createNewEpic(epicTask1);
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        subTask1.setStatus(Status.NEW);
        manager.createNewSubTask(subTask1);
        Subtask subTask2 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        subTask2.setStatus(Status.NEW);
        manager.createNewSubTask(subTask2);
        Subtask subTask3 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));
        subTask3.setStatus(Status.NEW);
        manager.createNewSubTask(subTask3);
        subTask1.setStatus(Status.DONE);
        manager.updateSubTask(subTask1);
        subTask2.setStatus(Status.DONE);
        manager.updateSubTask(subTask2);
        assertEquals(Status.IN_PROGRESS, epicTask1.getStatus());
    }

    @Test
    void epicWithInProgressStatusInSubTasks() {
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        int epicId = manager.createNewEpic(epicTask1);
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        subTask1.setStatus(Status.IN_PROGRESS);
        manager.createNewSubTask(subTask1);
        Subtask subTask2 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        subTask2.setStatus(Status.IN_PROGRESS);
        manager.createNewSubTask(subTask2);
        Subtask subTask3 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId, Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));
        subTask3.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, epicTask1.getStatus());
    }

    @Test
    void subTaskNoItsEpic() {
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", 1, Duration.ofMinutes(25), LocalDateTime.now());
        subTask1.setId(1);
        int subTask1Id =  manager.createNewSubTask(subTask1);
        assertEquals(-1, subTask1Id);
    }

    @Test
    void checkSavePreviousTask() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(30), LocalDateTime.now());
        int id = manager.createNewTask(task1);
        manager.getTask(id);
        List<Task> historyTasks = manager.getHistory();
        assertEquals(Status.NEW, historyTasks.get(0).getStatus());

    }
}
