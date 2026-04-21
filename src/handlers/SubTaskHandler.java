package handlers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import models.Subtask;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import exceptions.OverLapException;
import adapters.LocalDateTimeAdapter;
import adapters.DurationAdapter;

public class SubTaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    Gson gson;

    public SubTaskHandler(TaskManager manager) {
        this.manager = manager;
        this.gson =  new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        EndPoint endPoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        try {

            switch (endPoint) {

                case EndPoint.GET_SUBTASKS:
                    getSubTasks(exchange);
                    break;

                case EndPoint.CREATE_SUBTASK:
                    createSubTasks(exchange);
                    break;

                case EndPoint.DELETE_SUBTASK:
                    deleteSubTask(exchange);
                    break;

                case EndPoint.GET_SUBTASK_BY_ID:
                    getSubTaskById(exchange);
                    break;

                case EndPoint.UPDATE_SUBTASK:
                    updateSubTask(exchange);
                    break;

                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendText(exchange, e.getMessage(), ResponseCode.INTERNAL_ERROR);
        }
    }

    private EndPoint getEndpoint(String requestPath, String requestedMethod) {
        String[] paths = requestPath.split("/");

        if (paths.length == 2 && paths[1].equals("subtasks")) {
            if (requestedMethod.equals("GET")) return EndPoint.GET_SUBTASKS;
            if (requestedMethod.equals("POST")) return EndPoint.CREATE_SUBTASK;
        } else if (paths.length == 4 && paths[1].equals("subtasks") && paths[2].equals("id")) {
            if (requestedMethod.equals("DELETE")) return EndPoint.DELETE_SUBTASK;
            if (requestedMethod.equals("GET"))  return EndPoint.GET_SUBTASK_BY_ID;
            if (requestedMethod.equals("POST")) return EndPoint.UPDATE_SUBTASK;
        }
        return EndPoint.UNKNOWN;
    }

    private void getSubTasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = manager.getAllSubTasks();
        String response = gson.toJson(subtasks);
        sendText(exchange, response, ResponseCode.OK);
    }

    private void createSubTasks(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        try {
            manager.createNewSubTask(subtask);
            sendText(exchange, "created", ResponseCode.CREATED);
        } catch (OverLapException o) {
            sendHasInteractions(exchange);
        }
    }

    private void deleteSubTask(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        String[] parts = requestPath.split("/");
        int idToDelete = Integer.parseInt(parts[3]);
        manager.deleteSubTask(idToDelete);
        sendText(exchange, "deleted", ResponseCode.OK);
    }

    private void getSubTaskById(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        String[] parts = requestPath.split("/");
        int idToGetById = Integer.parseInt(parts[3]);
        Subtask subtaskToGetById = manager.getSubTask(idToGetById);
        if (subtaskToGetById != null) {
            String responseToGetById = gson.toJson(subtaskToGetById);
            sendText(exchange, responseToGetById, ResponseCode.OK);
        } else {
            sendNotFound(exchange);
        }
    }

    private void updateSubTask(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtaskUpdate = gson.fromJson(body, Subtask.class);

        try {
            manager.updateSubTask(subtaskUpdate);
            sendText(exchange, "updated", ResponseCode.CREATED);
        } catch (OverLapException o) {
            sendHasInteractions(exchange);
        }
    }
}
