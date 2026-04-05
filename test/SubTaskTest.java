import manager.Managers;
import manager.TaskManager;
import models.Subtask;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubTaskTest {

    @Test
    void subTaskNoItsEpic() {
        TaskManager manager = Managers.getDefault();
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", 1, Duration.ofMinutes(25), LocalDateTime.now());
        subTask1.setId(1);
        int subTask1Id =  manager.createNewSubTask(subTask1);
        assertEquals(-1, subTask1Id);
    }
}
