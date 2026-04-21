import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import handlers.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import adapters.LocalDateTimeAdapter;
import adapters.DurationAdapter;

public class HttpSubTasksTest {
    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @DisplayName("Добавление валидной подзадачи: сервер должен вернуть статус 201 ")
    @Test
    public void addSubTask_validTask_returnStatus201() throws IOException, InterruptedException {
        Epic epic = TestData.newEpic();
        manager.createNewEpic(epic);
        Subtask subTask = TestData.newSubTask(epic.getId());
        String subTaskToJson = gson.toJson(subTask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @DisplayName("Добавление подзадач с пересечением по времени: сервер должен вернуть статус 406")
    @Test
    public void addSubTasks_overLap_returnStatus406() throws IOException, InterruptedException {
        Epic epic = TestData.newEpic();
        manager.createNewEpic(epic);
        Subtask subTask = TestData.newSubTask(epic.getId());
        manager.createNewSubTask(subTask);
        Subtask subTask2 = TestData.newSubTask(epic.getId());
        String subTaskToJson = gson.toJson(subTask2);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @DisplayName("Получение всех подзадач: сервер должен вернуть статус 200")
    @Test
    public void getSubTasks_existingSubTasks_returnSListWithStatus200() throws IOException, InterruptedException {
        Epic epic = TestData.newEpic();
        manager.createNewEpic(epic);
        Subtask subTask = TestData.newSubTask(epic.getId());
        String subTaskToJson1 = gson.toJson(subTask);
        Subtask subTask2 = TestData.newStartTimeSubTask(epic.getId());
        String subTaskToJson2 = gson.toJson(subTask2);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskToJson1))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskToJson2))
                .build();

        client.send(request1, HttpResponse.BodyHandlers.ofString());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Subtask> list = manager.getAllSubTasks();
        assertEquals(2, list.size());
    }

    @DisplayName("Удаление существующей подзадачи: сервер должен вернуть статус 200")
    @Test
    public void deleteSubTask_existingSubTask_returnStatus200() throws IOException, InterruptedException {
        Epic epic = TestData.newEpic();
        manager.createNewEpic(epic);
        Subtask subTask = TestData.newSubTask(epic.getId());
        manager.createNewSubTask(subTask);
        int subTaskId = subTask.getId();
        URI url = URI.create("http://localhost:8080/subtasks/id/" + subTaskId);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @DisplayName("Получение истории просмотра: сервер должен вернуть статус 200")
    @Test
    public void getHistory_existingHistory_returnListWithStatusCode200() throws IOException, InterruptedException {
        Epic epic = TestData.newEpic();
        manager.createNewEpic(epic);
        Subtask subTask = TestData.newSubTask(epic.getId());
        manager.createNewSubTask(subTask);
        int subTaskId = subTask.getId();
        URI url = URI.create("http://localhost:8080/subtasks/id/" + subTaskId);
        URI historyUrl =  URI.create("http://localhost:8080/history");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(historyUrl)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> taskList = manager.getHistory();
        assertEquals(1, taskList.size());
    }
}
