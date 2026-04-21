import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import handlers.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import models.Epic;
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

public class HttpEpicsTest {
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

    @DisplayName("Добавление валидного эпика: сервер должен вернуть статус 201")
    @Test
    public void addEpic_validEpic_returnStatus201() throws IOException, InterruptedException {
        Epic epic = TestData.newEpic();
        String epicToJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @DisplayName("Получение всех эпиков: сервер должен вернуть статус 200")
    @Test
    public void getEpics_existingEpics_returnListWithStatus200() throws IOException, InterruptedException {
        Epic epic = TestData.newEpic();
        String epic2ToJson = gson.toJson(epic);
        Epic epic1 = TestData.newEpic();
        String epic1ToJson = gson.toJson(epic1);
        URI url = URI.create("http://localhost:8080/epics");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epic1ToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epic2ToJson))
                .build();

        HttpResponse<String> response2 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response1 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());
        List<Epic> epicsResponse = manager.getAllEpics();
        assertEquals(2, epicsResponse.size());
    }

    @DisplayName("Удаление существующего эпика: сервер должен вернуть статус 200")
    @Test
    public void deleteEpic_existingEpic_returnStatus200() throws IOException, InterruptedException {
        Epic epic = TestData.newEpic();
        manager.createNewEpic(epic);
        int id = epic.getId();
        URI url = URI.create("http://localhost:8080/epics/id/" + id);
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
    public void getHistory_existingHistory_returnListWithStatus200() throws IOException, InterruptedException {
        Epic epic = TestData.newEpic();
        manager.createNewEpic(epic);
        int id = epic.getId();
        URI url = URI.create("http://localhost:8080/epics/id/" + id);
        URI urlHistory = URI.create("http://localhost:8080/history");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(urlHistory)
                .GET()
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());
        List<Task> epicList = manager.getHistory();
        assertEquals(1, epicList.size());
    }
}
