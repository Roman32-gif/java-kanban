import manager.*;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryManagerTest {
    HistoryManager history = new InMemoryHistoryManager();

    @Test
    void addingToHistoryTest() {
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        epicTask1.setId(1);
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(30), LocalDateTime.now());
        task1.setId(2);
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", 1, Duration.ofMinutes(40), LocalDateTime.now());
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
    public void deleteFromHistory() {
        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        epicTask1.setId(1);
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(35), LocalDateTime.now());
        task1.setId(2);
        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", 1, Duration.ofMinutes(55), LocalDateTime.now());
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
    public void automaticDeleteTheSameTask() {
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(15), LocalDateTime.now());
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

    @Test
    void removeFromStartAndEnd() {
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(15), LocalDateTime.now().plusHours(1));
        task1.setId(1);
        Task task2 = new Task("Сделать уроки", "Написать сочинение",  Duration.ofMinutes(15), LocalDateTime.now().plusHours(2));
        task2.setId(2);
        Task task3 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(15), LocalDateTime.now().plusHours(3));
        task3.setId(3);
        history.add(task1);
        history.add(task2);
        history.add(task3);

        history.remove(1);
        assertEquals(2, history.getHistory().size());
        assertEquals(task2, history.getHistory().get(0), "После удаления первой, вторая должна стать первой");

        history.remove(3);
        assertEquals(1, history.getHistory().size());
        assertEquals(task2, history.getHistory().get(0), "Должна остаться только задача 2");
    }

    @Test
    void emptyHistory() {
        assertTrue(history.getHistory().isEmpty());
    }
}
