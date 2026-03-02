import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpicTest {

    @Test
    void epicNoAddLikeSubTask() {
        TaskManager manager = Managers.getDefault();
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        int epicId = manager.createNewEpic(epicTask1);
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId);
        subTask1.setId(epicId);
        int subTask1Id = manager.createNewSubTask(subTask1);
        assertEquals(-1, subTask1Id);
        Epic newEpic = manager.getEpic(epicId);
        assertTrue(newEpic.getSubtaskIds().isEmpty());
    }
}
