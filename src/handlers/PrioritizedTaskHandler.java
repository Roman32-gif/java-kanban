package handlers;
import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class PrioritizedTaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    Gson gson;

    public PrioritizedTaskHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        EndPoint endPoint = getEndpoint(exchange.getRequestURI().getPath());

        try {
            if (Objects.requireNonNull(endPoint) == EndPoint.GET_PRIORITIZED_TASKS) {
                String response = gson.toJson(manager.getPrioritizedTasks());
                sendText(exchange, response,ResponseCode.OK);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendText(exchange, e.getMessage(), ResponseCode.INTERNAL_ERROR);
        }
    }

    private EndPoint getEndpoint(String requestPath) {
        String[] paths = requestPath.split("/");

        if (paths.length == 2 && paths[1].equals("prioritized")) {
            return EndPoint.GET_PRIORITIZED_TASKS;
        }
        return EndPoint.UNKNOWN;
    }
}
