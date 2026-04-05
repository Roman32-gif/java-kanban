import manager.FileBackedTaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private File tempfile;

    @BeforeEach
    void setUp() {
        tempfile = new File("test.csv");
    }

    @AfterEach
    void deleteFile() {
        if (tempfile.exists()) {
            tempfile.delete();
        }
    }

    @Test
    public void saveToUnknownFileTest() {
        File file = new File("L:/test/TestFile12.CSV");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        FileBackedTaskManager.ManagerSaveException e = assertThrows(FileBackedTaskManager.ManagerSaveException.class, () ->{
                manager.createNewTask(new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(40), LocalDateTime.now()));});
        String expectedException = "Ошибка при записи в файл " + file;
        assertEquals(expectedException, e.getMessage(), "Другой текст исключения");
        }

    @Test
    public void loadFromUnknownFile() {
        File file = new File("L:/test/TestFile1.CSV");
        FileBackedTaskManager.ManagerSaveException e = assertThrows(FileBackedTaskManager.ManagerSaveException.class, () -> {
                    FileBackedTaskManager.loadFromFile(file);
                });
        String expectedException = "Ошибка при считывании файла " + file;
        assertEquals(expectedException, e.getMessage(), "Другой текст исключения");
    }

    @Test
    public void correctSaveToFileAndLoadFromFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempfile);
        Task task1 = new Task("Сделать уроки", "Написать сочинение", Duration.ofMinutes(30), LocalDateTime.now());
        int taskId1 = manager.createNewTask(task1);
        Task task2 = new Task("Сделать ремонт", "Поклеить обои", Duration.ofMinutes(50), LocalDateTime.now().plusHours(1));
        int taskId2 = manager.createNewTask(task2);
        manager.save();
        FileBackedTaskManager loaderManager = FileBackedTaskManager.loadFromFile(tempfile);
        assertEquals(2, loaderManager.getAllTasks().size(), "Неправильное количество задач");
        assertEquals(task1.getName(), loaderManager.getTask(task1.getId()).getName(), "Другое имя задачи");
    }

    @Test
    public void correctEpicInSubtaskFromFile() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempfile);
        Epic epic = new Epic("Подготовка к новому году", "Украсить дом");
        manager.createNewEpic(epic);
        int epicId = epic.getId();
        Subtask subtask = new Subtask("Купить ёлку", "Высота ёлки 1 метр", epicId, Duration.ofMinutes(20), LocalDateTime.now());
        manager.createNewSubTask(subtask);
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(tempfile);
        Epic epic1 = loadManager.getEpic(epicId);
        assertTrue(epic1.getSubtaskIds().contains(subtask.getId()));
    }
}
