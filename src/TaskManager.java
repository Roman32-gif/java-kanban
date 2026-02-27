import java.util.List;

public interface TaskManager {

    int createNewTask(Task task);

    int createNewEpic(Epic epic);

    int createNewSubTask(Subtask subtask);

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
