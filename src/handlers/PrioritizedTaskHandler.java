package handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import java.io.IOException;
import java.util.Objects;

public class PrioritizedTaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    Gson gson;

    public PrioritizedTaskHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        EndPoint endPoint = getEndpoint(exchange.getRequestURI().getPath());

        try {
            if (Objects.requireNonNull(endPoint) == EndPoint.GET_PRIORITIZED_TASKS) {
                String response = gson.toJson(manager.getPrioritizedTasks());
                sendText(exchange, response,200);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendText(exchange, e.getMessage(), 500);
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
