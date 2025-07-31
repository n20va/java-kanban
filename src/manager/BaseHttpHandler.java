package manager;


import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected void sendText(HttpExchange exchange, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        } finally {
            exchange.close();
        }
    }

    protected void sendNotFound(HttpExchange exchange) {
        sendStatus(exchange, 404, "Not Found");
    }

    protected void sendHasOverlaps(HttpExchange exchange) {
        sendStatus(exchange, 406, "Task time overlaps with existing task");
    }

    protected void sendInternalError(HttpExchange exchange) {
        sendStatus(exchange, 500, "Internal Server Error");
    }

    private void sendStatus(HttpExchange exchange, int code, String message) {
        try {
            byte[] resp = message.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(code, resp.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при отправке ответа: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    protected void sendError(HttpExchange exchange, int statusCode, String message) {
        try {
            String json = "{\"error\":\"" + message + "\"}";
            sendText(exchange, json, statusCode);
        } catch (IOException ignored) {}
    }
}
