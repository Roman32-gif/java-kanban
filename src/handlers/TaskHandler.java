package handlers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import models.Task;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import exceptions.OverLapException;
import adapters.LocalDateTimeAdapter;
import adapters.DurationAdapter;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    Gson gson;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        EndPoint endPoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        try {

            switch (endPoint) {

                case EndPoint.GET_TASKS:
                   getTasks(exchange);
                    break;

                case EndPoint.CREATE_TASK:
                    createTasks(exchange);
                    break;

                case EndPoint.GET_TASK_BY_ID:
                    getTasksById(exchange);
                    break;

                case EndPoint.UPDATE_TASK:
                   updateTask(exchange);
                    break;

                case EndPoint.DELETE_TASK:
                    deleteTask(exchange);
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

        if (paths.length == 2 && paths[1].equals("tasks")) {
            if (requestedMethod.equals("GET")) return EndPoint.GET_TASKS;
            if (requestedMethod.equals("POST")) return EndPoint.CREATE_TASK;
        } else if (paths.length == 4 && paths[1].equals("tasks") && paths[2].equals("id")) {
            if (requestedMethod.equals("GET"))  return EndPoint.GET_TASK_BY_ID;
            if (requestedMethod.equals("POST"))  return EndPoint.UPDATE_TASK;
            if (requestedMethod.equals("DELETE"))  return EndPoint.DELETE_TASK;
        }
        return EndPoint.UNKNOWN;
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getAllBaseTasks();
        String response = gson.toJson(tasks);
        sendText(exchange, response, ResponseCode.OK);
    }

    private void createTasks(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        Task taskCreate = gson.fromJson(body, Task.class);
        try {
            manager.createNewTask(taskCreate);
            sendText(exchange, "created", ResponseCode.CREATED);
        } catch (OverLapException o) {
            sendHasInteractions(exchange);
        }
    }

    private void getTasksById(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        String[] parts = requestPath.split("/");
        int idToGet = Integer.parseInt(parts[3]);
        Task taskFindById = manager.getTask(idToGet);
        if (taskFindById != null) {
            String responseToFindById = gson.toJson(taskFindById);
            sendText(exchange, responseToFindById,ResponseCode.OK);
        } else {
            sendNotFound(exchange);
        }
    }

    private void updateTask(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        Task newTask = gson.fromJson(body, Task.class);
        try {
            manager.updateBaseTask(newTask);
            sendText(exchange, "updated", ResponseCode.CREATED);
        } catch (OverLapException o) {
            sendHasInteractions(exchange);
        }
    }

    private void deleteTask(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        String[] parts = requestPath.split("/");
        int idToDelete = Integer.parseInt(parts[3]);
        manager.deleteBasicTask(idToDelete);
        sendText(exchange, "deleted", ResponseCode.OK);
    }
}
