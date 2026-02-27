import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> baseTasks = new HashMap<>();
    private final Map<Integer, Epic> epicTasks = new HashMap<>();
    private final Map<Integer, Subtask> subTasks = new HashMap<>();
    private int idCounter = 1;
    private final HistoryManager history = Managers.getDefaultHistory();

    private int generateId() {
        return idCounter++;
    }

    private int addBaseTasks(Task task) {
        int id = generateId();
        task.setId(id);
        baseTasks.put(id, task);
        return id;
    }


    private int addEpicTasks(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epicTasks.put(id, epic);
        return id;
    }

    private int addSubTask(Subtask subtask) {
        Epic epic = epicTasks.get(subtask.getEpicId());

        if (epic == null) {
            return -1;
        }

        if (subtask.getId() == subtask.getEpicId()) {
            return -1;
        }

        int id = generateId();
        subtask.setId(id);
        subTasks.put(id, subtask);
        epic.addSubtask(id);
        updateEpicStatus(epic.getId());
        return id;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(baseTasks.values());
        allTasks.addAll(epicTasks.values());
        allTasks.addAll(subTasks.values());
        return allTasks;
    }

    public void deleteAllTasks() {
        baseTasks.clear();
        epicTasks.clear();
        subTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {

        if (baseTasks.containsKey(id)) {
            return baseTasks.get(id);
        } else if (epicTasks.containsKey(id)) {
            return epicTasks.get(id);
        } else if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        }
        return null;
    }

    @Override
    public int createNewTask(Task task) {
        return addBaseTasks(task);
    }

    @Override
    public int createNewEpic(Epic epic) {
        return addEpicTasks(epic);
    }

    @Override
    public int createNewSubTask(Subtask subtask) {
        return addSubTask(subtask);
    }

    @Override
    public void updateBaseTask(Task updatedTask) {
        int id = updatedTask.getId();
        if (baseTasks.containsKey(id)) {
            baseTasks.put(id, updatedTask);
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        int id = updatedEpic.getId();
        if (epicTasks.containsKey(id)) {
            epicTasks.put(id, updatedEpic);
            updateEpicStatus(id);
        }
    }

    @Override
    public void updateSubTask(Subtask updatedSubtask) {
        int id = updatedSubtask.getId();
        Subtask oldSubTask = subTasks.get(id);
        if (oldSubTask == null) {
            return;
        }

        int newEpicId = updatedSubtask.getEpicId();
        int oldEpicId = oldSubTask.getEpicId();

        subTasks.put(id, updatedSubtask);

        if (oldEpicId != newEpicId) {
            Epic oldEpic = epicTasks.get(oldEpicId);
            if (oldEpic != null) {
                oldEpic.getSubtaskIds().remove((Integer) id);
                updateEpicStatus(oldEpicId);
            }
            Epic newEpic = epicTasks.get(newEpicId);
            if (newEpic != null) {
                newEpic.addSubtask(id);
                updateEpicStatus(newEpicId);
            }
        } else {
            updateEpicStatus(newEpicId);
        }
    }

    @Override
    public void deleteBasicTask(int id) {
        if (baseTasks.containsKey(id)) {
            baseTasks.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epicTasks.get(id);

        if (epic == null) {
            return;
        }

        for (Integer subtaskId : epic.getSubtaskIds()) {
            subTasks.remove(subtaskId);
        }

        epicTasks.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        Subtask subtask = subTasks.remove(id);
        if (subtask == null) {
            return;
        }

        int epicId = subtask.getEpicId();
        Epic epic = epicTasks.get(epicId);

        if (epic != null) {
            epic.getSubtaskIds().remove((Integer) id);
            updateEpicStatus(epicId);
        }
    }

    public List<Subtask> showAllSubTasksByEpic(int id) {

        if (!epicTasks.containsKey(id)) {
            return new ArrayList<>();
        }

        Epic currentEpic = epicTasks.get(id);
        List<Subtask> subTask = new ArrayList<>();

        for (Integer subTaskId : currentEpic.getSubtaskIds()) {
            subTask.add(subTasks.get(subTaskId));
        }
        return subTask;
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = epicTasks.get(epicId);
        if (epic == null) {
            return;
        }

        List<Integer> subTasksId = epic.getSubtaskIds();

        if (subTasksId == null || subTasksId.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int subTaskId : subTasksId) {
            Subtask subtask = subTasks.get(subTaskId);
            if (subtask == null) {
                allNew = false;
                allDone = false;
                continue;
            }

            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }

            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


    public Task getTask(int id) {
        Task task = baseTasks.get(id);
        if (task != null) {
           history.add(task.copy());
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epicTasks.get(id);
        if (epic != null) {
            history.add(epic.copy());
        }
        return epic;
    }

    @Override
    public Subtask getSubTask(int id) {
        Subtask subtask = subTasks.get(id);
        if (subtask != null) {
           history.add(subtask.copy());
        }
        return subtask;
    }

    public List<Task> getHistory() {
        return history.getHistory();
    }

}
