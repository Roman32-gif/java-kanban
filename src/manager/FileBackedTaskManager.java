package manager;
import models.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write('\ufeff');
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : super.baseTasks.values()){
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : super.epicTasks.values()) {
                writer.write(toString(epic) + "\n");
            }

            for (Subtask subtask : super.subTasks.values()) {
                writer.write(toString(subtask) + "\n");
            }

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

        return task.getId() + "," + type + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + epicId;
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

            fileBackedTaskManager.idCounter = maxId;

        } catch (IOException o){
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
        int epicId = 0;

        if (type.equals(Types.SUBTASK)) {
            epicId = Integer.parseInt(parseString[5]);
        }

        switch (type) {
            case TASK:
                Task newTask = new Task(name,description);
                newTask.setId(id);
                newTask.setStatus(status);
                return newTask;

            case EPIC:
                Epic newEpic = new Epic(name, description);
                newEpic.setId(id);
                newEpic.setStatus(status);
                return newEpic;

            case SUBTASK:
                Subtask newSubtask = new Subtask(name, description, epicId);
                newSubtask.setId(id);
                newSubtask.setStatus(status);
                return newSubtask;

            default:
                throw new IllegalArgumentException("Неизвестный тип " + type);
        }
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
