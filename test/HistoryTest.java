import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryTest {
    HistoryManager history = new InMemoryHistoryManager();


    @Test
    void addingToHistoryTest () {
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        epicTask1.setId(1);
        Task task1 = new Task("Сделать уроки", "Написать сочинение");
        task1.setId(2);
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", 1);
        subTask1.setId(3);
        history.add(epicTask1);
        history.add(task1);
        history.add(subTask1);
        List<Task> historyTasks = new ArrayList<>(history.getHistory());
        assertEquals (3, historyTasks.size());
        assertEquals(epicTask1, historyTasks.get(0));
        assertEquals(task1, historyTasks.get(1));
        assertEquals(subTask1, historyTasks.get(2));
    }

    @Test
    public void deleteFromHistory () {
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        epicTask1.setId(1);
        Task task1 = new Task("Сделать уроки", "Написать сочинение");
        task1.setId(2);
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", 1);
        subTask1.setId(3);
        history.add(epicTask1);
        history.add(task1);
        history.add(subTask1);
        List<Task> historyTasks = new ArrayList<>(history.getHistory());
        history.remove(2);
        historyTasks = new ArrayList<>(history.getHistory());
        assertEquals(2, historyTasks.size());
        assertEquals(epicTask1, historyTasks.get(0));
        assertEquals(subTask1, historyTasks.get(1));
    }

    @Test
    public void automaticDeleteTheSameTask () {
        Task task1 = new Task("Сделать уроки", "Написать сочинение");
        task1.setId(1);
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        epicTask1.setId(2);
        history.add(task1);
        history.add(epicTask1);
        history.add(task1);
        List<Task> historyTasks = new ArrayList<>(history.getHistory());
        System.out.println(historyTasks);
        assertEquals(2, historyTasks.size());
        assertEquals(epicTask1, historyTasks.get(0));
        assertEquals(task1, historyTasks.get(1));
    }
}
