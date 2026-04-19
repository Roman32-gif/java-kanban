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

public class HttpSubTasksTest {
    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new HttpTaskServer.LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new HttpTaskServer.DurationAdapter())
                .create();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void addSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("test1", "testing1");
        manager.createNewEpic(epic);
        int epicId = epic.getId();
        Subtask subTask = new Subtask("test1", "test1", epicId, Duration.ofMinutes(15), LocalDateTime.now());
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

    @Test
    public void addOverLoop() throws IOException, InterruptedException {
        Epic epic = new Epic("test1", "testing1");
        manager.createNewEpic(epic);
        int epicId = epic.getId();
        Subtask subTask = new Subtask("test1", "test1", epicId, Duration.ofMinutes(15), LocalDateTime.now());
        manager.createNewSubTask(subTask);
        Subtask subTask2 = new Subtask("test1", "test1", epicId, Duration.ofMinutes(15), LocalDateTime.now());
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

    @Test
    public void getSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("test1", "testing1");
        manager.createNewEpic(epic);
        int epicId = epic.getId();
        Subtask subTask = new Subtask("test1", "test1", epicId, Duration.ofMinutes(15), LocalDateTime.now());
        String subTaskToJson1 = gson.toJson(subTask);
        Subtask subTask2 = new Subtask("test1", "test1", epicId, Duration.ofMinutes(15), LocalDateTime.now().plusHours(2));
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
    }

    @Test
    public void deleteSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("test1", "testing1");
        manager.createNewEpic(epic);
        int epicId = epic.getId();
        Subtask subTask = new Subtask("test1", "test1", epicId, Duration.ofMinutes(15), LocalDateTime.now());
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

    @Test
    public void getHistory() throws IOException, InterruptedException {
        Epic epic = new Epic("test1", "testing1");
        manager.createNewEpic(epic);
        int epicId = epic.getId();
        Subtask subTask = new Subtask("test1", "test1", epicId, Duration.ofMinutes(15), LocalDateTime.now());
        manager.createNewSubTask(subTask);
        int subTaskId = subTask.getId();
        URI url = URI.create("http://localhost:8080/subtasks/id/" + subTaskId);
        URI baseUrl =  URI.create("http://localhost:8080/subtasks");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(baseUrl)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> taskList = manager.getHistory();
        assertEquals(1, taskList.size());
    }
}
