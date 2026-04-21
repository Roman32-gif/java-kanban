package handlers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected void sendText(HttpExchange exchange, String text, ResponseCode responseCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode.getValue(), resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "Не найдено", ResponseCode.NOT_FOUND);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, "Пересекается с уже существующими", ResponseCode.HAS_OVERLAP);
    }
}
