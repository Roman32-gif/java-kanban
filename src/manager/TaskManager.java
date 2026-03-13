package manager;
import models.Epic;
import models.Subtask;
import models.Task;

import java.io.IOException;
import java.util.List;

public interface TaskManager {

    int createNewTask(Task task) throws IOException;

    int createNewEpic(Epic epic) throws IOException;

    int createNewSubTask(Subtask subtask) throws IOException;

    void updateBaseTask(Task updatedTask);

    void updateEpic(Epic updatedEpic);

    void updateSubTask(Subtask updatedSubtask);

    void deleteBasicTask(int id);

    void deleteEpic(int id);

    void deleteSubTask(int id);

    List<Task> getAllTasks();

    Task getTaskById(int id);

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubTask(int id);


}
