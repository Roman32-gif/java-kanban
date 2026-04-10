package manager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> baseTasks = new HashMap<>();
    protected final Map<Integer, Epic> epicTasks = new HashMap<>();
    protected final Map<Integer, Subtask> subTasks = new HashMap<>();
    protected final  TreeSet<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime).thenComparingInt(Task::getId));
    protected int idCounter = 1;
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
        updateInfoAboutTime(epic);
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

    @Override
    public void deleteAllTasks() {
        List<Integer> allId = new ArrayList<>();
        allId.addAll(baseTasks.keySet());
        allId.addAll(epicTasks.keySet());
        allId.addAll(subTasks.keySet());

        allId.forEach(history::remove);

        baseTasks.clear();
        epicTasks.clear();
        subTasks.clear();
        sortedTasks.clear();
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

        if (checkIntersections(task)) {
            throw new FileBackedTaskManager.ManagerSaveException("Задачи пересекаются по времени: " + task.getStartTime());
        }

        int base = addBaseTasks(task);
        if (task.getStartTime() != null) {
            sortedTasks.add(task);
        }
        return base;
    }

    @Override
    public int createNewEpic(Epic epic) {

        return addEpicTasks(epic);
    }

    @Override
    public int createNewSubTask(Subtask subtask) {

        if (checkIntersections(subtask)) {
            throw new FileBackedTaskManager.ManagerSaveException("Задачи пересекаются по времени: " + subtask.getStartTime());
        }

        int sub = addSubTask(subtask);
        if (subtask.getStartTime() != null) {
            sortedTasks.add(subtask);
        }
        return sub;
    }

    @Override
    public void updateBaseTask(Task updatedTask) {

        if (checkIntersections(updatedTask)) {
            throw new FileBackedTaskManager.ManagerSaveException("Задачи пересекаются по времени: " + updatedTask.getStartTime());
        }

        int id = updatedTask.getId();
        if (baseTasks.containsKey(id)) {
            Task priviousTask = baseTasks.get(id);
            sortedTasks.remove(priviousTask);
            baseTasks.put(id, updatedTask);
            if (updatedTask.getStartTime() != null) {
                sortedTasks.add(updatedTask);
            }
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

        if (checkIntersections(updatedSubtask)) {
            throw new FileBackedTaskManager.ManagerSaveException("Задачи пересекаются по времени: " + updatedSubtask.getStartTime());
        }

        int id = updatedSubtask.getId();
        Subtask oldSubTask = subTasks.get(id);
        if (oldSubTask == null) {
            return;
        }

        sortedTasks.remove(oldSubTask);
        int newEpicId = updatedSubtask.getEpicId();
        int oldEpicId = oldSubTask.getEpicId();

        subTasks.put(id, updatedSubtask);

        if (oldEpicId != newEpicId) {
            Epic oldEpic = epicTasks.get(oldEpicId);
            if (oldEpic != null) {
                oldEpic.getSubtaskIds().remove((Integer) id);
                updateEpicStatus(oldEpicId);
                updateInfoAboutTime(oldEpic);
            }
            Epic newEpic = epicTasks.get(newEpicId);
            if (newEpic != null) {
                newEpic.addSubtask(id);
                updateEpicStatus(newEpicId);
                updateInfoAboutTime(newEpic);
            }
        } else {
            Epic epic = epicTasks.get(newEpicId);
            if (epic != null) {
                updateEpicStatus(newEpicId);
                updateInfoAboutTime(epic);
            }
        }
        if (updatedSubtask.getStartTime() != null) {
            sortedTasks.add(updatedSubtask);
        }
    }

    @Override
    public void deleteBasicTask(int id) {
        if (baseTasks.containsKey(id)) {
            sortedTasks.remove(baseTasks.get(id));
            baseTasks.remove(id);
            history.remove(id);
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
            history.remove(subtaskId);
            sortedTasks.remove(subtaskId);
        }

        epicTasks.remove(id);
        history.remove(id);
        sortedTasks.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        Subtask subtask = subTasks.remove(id);
        if (subtask == null) {
            return;
        }

        history.remove(id);
        sortedTasks.remove(subtask);
        int epicId = subtask.getEpicId();
        Epic epic = epicTasks.get(epicId);

        if (epic != null) {
            epic.getSubtaskIds().remove((Integer) id);
            updateEpicStatus(epicId);
            updateInfoAboutTime(epic);
        }
    }

    @Override
    public List<Subtask> showAllSubTasksByEpic(int id) {

        Epic currentEpic = epicTasks.get(id);
        if (!epicTasks.containsKey(id)) {
            return new ArrayList<>();
        }

        return currentEpic.getSubtaskIds().stream()
                .map(subTasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

        List<Status> statuses = subTasksId.stream()
                .map(subTasks::get)
                .filter(Objects::nonNull)
                .map(Subtask::getStatus)
                .toList();

        boolean allNew = statuses.stream().allMatch(s -> s == Status.NEW);
        boolean allDone = statuses.stream().allMatch(s -> s == Status.DONE);

        if (allNew) {
           epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
       }
    }

    @Override
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

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }


    private void updateInfoAboutTime(Epic epic) {
        List<Integer> subTaskIds = epic.getSubtaskIds();

        if (subTaskIds.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
            epic.setStartTime(null);
            return;
        }

        Duration duration = Duration.ZERO;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        for (Integer id : subTaskIds) {
            Subtask subtask = subTasks.get(id);
            if (subtask.getStartTime() == null) {
                continue;
            }
            duration = duration.plus(subtask.getDuration());

            if (startTime == null || subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }

            if (endTime == null || subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }

        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasks);
    }

    @Override
    public boolean checkIntersections(Task task) {

        if (task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }

        return sortedTasks.stream()
                .filter(taskFromTree -> task.getId() != taskFromTree.getId())
                .anyMatch(taskFromTree -> {
                    LocalDateTime start = task.getStartTime();
                    LocalDateTime end = task.getEndTime();
                    LocalDateTime start2 = taskFromTree.getStartTime();
                    LocalDateTime end2 = taskFromTree.getEndTime();
                    return start.isBefore(end2) && start2.isBefore(end);
                });
    }
}
