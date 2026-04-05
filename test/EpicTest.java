import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpicTest {

    @Test
    void epicNoAddLikeSubTask() {
        TaskManager manager = Managers.getDefault();
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
         TaskManager manager = Managers.getDefault();
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
         TaskManager manager = Managers.getDefault();
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
         TaskManager manager = Managers.getDefault();
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
         TaskManager manager = Managers.getDefault();
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
}
