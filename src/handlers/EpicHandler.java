package handlers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager manager;
    Gson gson;

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
        this.gson =  new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new HttpTaskServer.LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new HttpTaskServer.DurationAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        EndPoint endPoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        String requestPath = exchange.getRequestURI().getPath();
        String[] parts = requestPath.split("/");
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        try {

            switch (endPoint) {

                case EndPoint.GET_EPICS:
                    List<Epic> epics = manager.getAllEpics();
                    String response = gson.toJson(epics);
                    sendText(exchange, response, 200);
                    break;

                case EndPoint.CREATE_EPIC:
                    Epic epic = gson.fromJson(body, Epic.class);
                    manager.createNewEpic(epic);
                    sendText(exchange, "created", 201);
                    break;

                case EndPoint.GET_EPIC_BY_ID:
                    int id = Integer.parseInt(parts[3]);
                    Epic epicToGetById = manager.getEpic(id);
                    if (epicToGetById != null) {
                        String responseToGetById = gson.toJson(epicToGetById);
                        sendText(exchange, responseToGetById, 200);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;

                case EndPoint.DELETE_EPIC:
                    int idToDelete = Integer.parseInt(parts[3]);
                    manager.deleteEpic(idToDelete);
                    sendText(exchange, "deleted", 200);
                    break;

                case EndPoint.GET_EPIC_SUBTASKS:
                    int idEpic = Integer.parseInt(parts[3]);
                    if (manager.getEpic(idEpic) != null) {
                        List<Subtask> subtasks = manager.showAllSubTasksByEpic(idEpic);
                        String responseSubtasks = gson.toJson(subtasks);
                        sendText(exchange, responseSubtasks, 200);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;

                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendText(exchange, e.getMessage(), 500);
        }
    }

    private EndPoint getEndpoint(String requestPath, String requestedMethod) {
        String[] paths = requestPath.split("/");

        if (paths.length == 2 && paths[1].equals("epics")) {
            if (requestedMethod.equals("GET")) return EndPoint.GET_EPICS;
            if (requestedMethod.equals("POST")) return EndPoint.CREATE_EPIC;
        } else if (paths.length == 4 && paths[1].equals("epics") && paths[2].equals("id")) {
            if (requestedMethod.equals("GET")) return EndPoint.GET_EPIC_BY_ID;
            if (requestedMethod.equals("DELETE")) return EndPoint.DELETE_EPIC;
        } else if (paths.length == 6 && paths[1].equals("epics") && paths[2].equals("id") && paths[5].equals("subtasks")) {
            return EndPoint.GET_EPIC_SUBTASKS;
        }
        return EndPoint.UNKNOWN;
    }
}


