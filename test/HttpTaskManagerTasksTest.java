import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import handlers.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import models.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {

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
    public void addTaskCorrect() throws IOException, InterruptedException {
        Task task = new Task("test1", "testing1", Duration.ofMinutes(15), LocalDateTime.now());
        String taskToJson = gson.toJson(task);
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, httpResponse.statusCode());

        List<Task> taskFromManager = manager.getAllTasks();
        assertNotNull(taskFromManager, "Задачи не возвращаются");
        assertEquals(1, taskFromManager.size(), "Некорректное количество задач");
        assertEquals("test1", taskFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void addTasksToOverloop() throws IOException, InterruptedException {
        Task task = new Task("test1", "testing1", Duration.ofMinutes(15), LocalDateTime.now());
        manager.createNewTask(task);
        Task task1 = new Task("test2", "testing2", Duration.ofMinutes(15), LocalDateTime.now());
        String taskToJson2 = gson.toJson(task1);
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson2))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, httpResponse.statusCode());
    }

    @Test
    public void correctGetTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task task1 = new Task("test1", "testing1", Duration.ofMinutes(15), LocalDateTime.now());
        String taskToJson = gson.toJson(task1);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("test2", "testing2", Duration.ofMinutes(15), LocalDateTime.now().plusHours(2));
        String taskToJson2 = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest1 = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(taskToJson2))
                        .build();

        HttpResponse<String> httpResponse2 = client.send(httpRequest1, HttpResponse.BodyHandlers.ofString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> taskFromManager = manager.getAllTasks();
        assertEquals(2, taskFromManager.size());
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Task task = new Task("test1", "testing1", Duration.ofMinutes(15), LocalDateTime.now());
        manager.createNewTask(task);
        int id = task.getId();
        URI url = URI.create("http://localhost:8080/tasks/id/" + id);
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, httpResponse.statusCode());
        List<Task> tasks = manager.getAllTasks();
        assertEquals(0, tasks.size());
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        Task task = new Task("test1", "testing1", Duration.ofMinutes(15), LocalDateTime.now());
        manager.createNewTask(task);
        int id = task.getId();
        URI url = URI.create("http://localhost:8080/tasks/id/" + id);
        URI urlHistory = URI.create("http://localhost:8080/history");
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(urlHistory)
                .GET()
                .build();

        HttpResponse response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
    }
}
