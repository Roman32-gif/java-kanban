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

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    Gson gson;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = new GsonBuilder()
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

                case EndPoint.GET_TASKS:
                    List<Task> tasks = manager.getAllBaseTasks();
                    String response = gson.toJson(tasks);
                    sendText(exchange, response, 200);
                    break;

                case EndPoint.CREATE_TASK:
                    Task taskCreate = gson.fromJson(body, Task.class);
                    try{
                        manager.createNewTask(taskCreate);
                        sendText(exchange, "created", 201);
                    } catch (BaseHttpHandler.OverLapException o) {
                        sendHasInteractions(exchange);
                    }
                    break;

                case EndPoint.GET_TASK_BY_ID:
                    int idToGet = Integer.parseInt(parts[3]);
                    Task taskFindById = manager.getTask(idToGet);
                    if (taskFindById != null) {
                        String responseToFindById = gson.toJson(taskFindById);
                        sendText(exchange, responseToFindById,200);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;

                case EndPoint.UPDATE_TASK:
                    Task newTask = gson.fromJson(body, Task.class);
                    try {
                        manager.updateBaseTask(newTask);
                        sendText(exchange, "updated", 201);
                    } catch (BaseHttpHandler.OverLapException o) {
                        sendHasInteractions(exchange);
                    }
                    break;

                case EndPoint.DELETE_TASK:
                    int idToDelete = Integer.parseInt(parts[3]);
                    manager.deleteBasicTask(idToDelete);
                    sendText(exchange, "deleted", 200);
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
}
