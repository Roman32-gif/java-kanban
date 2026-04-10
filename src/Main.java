import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String file = "D://test/TestFile.CSV";
        FileBackedTaskManager fileManager = FileBackedTaskManager.loadFromFile(new File(file));
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(30), LocalDateTime.now());
        int taskId1 = manager.createNewTask(task1);

        Task task2 = new Task("Сделать ремонт", "Поклеить обои", Duration.ofMinutes(60), LocalDateTime.now());
        int taskId2 = manager.createNewTask(task2);

        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        int epicId1 = manager.createNewEpic(epicTask1);

        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId1, Duration.ofMinutes(20), LocalDateTime.now());
        manager.createNewSubTask(subTask1);
        Subtask subTask2 = new Subtask("Купить новогодние подарки", "5 подарков", epicId1, Duration.ofMinutes(50), LocalDateTime.now());
        manager.createNewSubTask(subTask2);

        Epic epicTask2 = new Epic("Подготовиться к экзамену", "Выучить все 40 устных вопросов");
        int epicId2 = manager.createNewEpic(epicTask2);
        Subtask subTask3 = new Subtask("Написать ответы на все 40 вопросов", "Взять информацию из учебника", epicId2, Duration.ofMinutes(40), LocalDateTime.now());
        manager.createNewSubTask(subTask3);

        System.out.println("Все задачи:");
        System.out.println(manager.getAllTasks());
        task2.setStatus(Status.DONE);
        manager.updateBaseTask(task2);
        subTask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask2);
        subTask3.setStatus(Status.DONE);
        manager.updateSubTask(subTask3);

        System.out.println("Задачи после изменений статусов");
        System.out.println(manager.getAllTasks());
        fileManager.save();
        List<Task> sorted = manager.getPrioritizedTasks();
        sorted.forEach(System.out::println);
        boolean answer = manager.checkIntersections(subTask3);
        System.out.println(answer);

    }
}
