import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubTaskTest {

    @Test
    void subTaskNoItsEpic() {
        TaskManager manager = Managers.getDefault();
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", 1);
        subTask1.setId(1);
        int subTask1Id =  manager.createNewSubTask(subTask1);
        assertEquals(-1, subTask1Id);

    }
}
