package manager;
import models.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write('\ufeff');
            writer.write("id,type,name,status,description,epic,duration,startTime\n");

            Stream.of(baseTasks.values(), epicTasks.values(), subTasks.values())
                    .flatMap(Collection::stream)
                    .map(this::toString)
                    .forEach(line -> {
                        try {
                            writer.write(line + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка при записи " + line);
                        }
                    });

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл " + file);
        }
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message) {
            super(message);
        }
    }

    public String toString(Task task) {
        Types type;
        String epicId = "";

        if (task instanceof Epic) {
            type = Types.EPIC;
        } else if (task instanceof Subtask) {
            type = Types.SUBTASK;
            epicId = String.valueOf(((Subtask) task).getEpicId());
        } else {
            type = Types.TASK;
        }

        return task.getId() + "," + type + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + epicId + "," + task.getDuration() + "," + task.getStartTime();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        int maxId = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                Task task = fileBackedTaskManager.fromString(line);
                int id = task.getId();

                if (task instanceof Epic) {
                    fileBackedTaskManager.epicTasks.put(id, (Epic) task);
                } else if (task instanceof Subtask) {
                    fileBackedTaskManager.subTasks.put(id, (Subtask) task);
                } else {
                    fileBackedTaskManager.baseTasks.put(id, task);
                }

                if (id > maxId) {
                    maxId = id;
                }
            }

            fileBackedTaskManager.subTasks.values().stream()
                    .forEach(subtask -> {
                        Epic epic = fileBackedTaskManager.epicTasks.get(subtask.getEpicId());
                        if (epic != null) {
                            epic.addSubtask(subtask.getId());
                        }
                    });

            fileBackedTaskManager.epicTasks.values().stream()
                    .map(Epic::getId)
                    .forEach(fileBackedTaskManager::calculateEpicStatus);

            fileBackedTaskManager.idCounter = maxId + 1;

        } catch (IOException o) {
            throw new ManagerSaveException("Ошибка при считывании файла " + file);
        }
        return fileBackedTaskManager;
    }

    private Task fromString(String value) {
        String [] parseString = value.split(",");
        int id = Integer.parseInt(parseString[0]);
        Types type = Types.valueOf(parseString[1]);
        String name = parseString[2];
        Status status = Status.valueOf(parseString[3]);
        String description = parseString[4];
        Duration duration = parseString[6].equals("null") ? Duration.ZERO : Duration.parse(parseString[6]);
        LocalDateTime startTime = parseString[7].equals("null") ? null : LocalDateTime.parse(parseString[7]);
        int epicId = 0;

        if (type.equals(Types.SUBTASK)) {
            epicId = Integer.parseInt(parseString[5]);
        }

        switch (type) {
            case TASK:
                Task newTask = new Task(name,description, duration, startTime);
                newTask.setId(id);
                newTask.setStatus(status);
                return newTask;

            case EPIC:
                Epic newEpic = new Epic(name, description);
                newEpic.setId(id);
                newEpic.setStatus(status);
                return newEpic;

            case SUBTASK:
                Subtask newSubtask = new Subtask(name, description, epicId, duration, startTime);
                newSubtask.setId(id);
                newSubtask.setStatus(status);
                return newSubtask;

            default:
                throw new ManagerSaveException("Неизвестный тип " + type);
        }
    }

    private void calculateEpicStatus(int epicId) {
        super.updateEpicStatus(epicId);
    }

    @Override
    public int createNewTask(Task task) {
        int id = super.createNewTask(task);
        save();
        return id;
    }

    @Override
    public int createNewEpic(Epic epic) {
        int id = super.createNewEpic(epic);
        save();
        return  id;
    }

    @Override
    public int createNewSubTask(Subtask subtask) {
        int id = super.createNewSubTask(subtask);
        save();
        return id;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteBasicTask(int id) {
        super.deleteBasicTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void updateBaseTask(Task updatedTask) {
        super.updateBaseTask(updatedTask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateEpicStatus(int epicId) {
        super.updateEpicStatus(epicId);
        save();
    }

    @Override
    public void updateSubTask(Subtask updatedSubtask) {
        super.updateSubTask(updatedSubtask);
        save();
    }
}
