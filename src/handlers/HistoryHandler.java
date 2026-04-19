package handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import java.io.IOException;
import java.util.Objects;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;
    Gson gson;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        EndPoint endPoint = getEndpoint(exchange.getRequestURI().getPath());

        try {
            if (Objects.requireNonNull(endPoint) == EndPoint.GET_HISTORY) {
                String response = gson.toJson(manager.getHistory());
                sendText(exchange, response, 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendText(exchange, e.getMessage(), 500);
        }
    }

    private EndPoint getEndpoint(String requestPath) {
        String[] paths = requestPath.split("/");

        if (paths.length == 2 && paths[1].equals("history")) {
            return EndPoint.GET_HISTORY;
        }
        return EndPoint.UNKNOWN;
    }
}
