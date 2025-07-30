package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET" -> sendText(exchange, gson.toJson(manager.getAllEpics()), 200);
                case "POST" -> {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);

                    if (epic.getId() == 0) {
                        manager.addEpic(epic);
                        sendText(exchange, "{\"id\":" + epic.getId() + "}", 201);
                    } else if (manager.getEpicById(epic.getId()) != null) {
                        manager.updateEpic(epic);
                        sendText(exchange, gson.toJson(epic), 201);
                    } else sendNotFound(exchange);
                }
                case "DELETE" -> {
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.startsWith("id=")) {
                        int id = Integer.parseInt(query.substring(3));
                        if (manager.getEpicById(id) != null) {
                            manager.removeEpicById(id);
                            sendText(exchange, "{\"status\":\"deleted\"}", 201);
                        } else sendNotFound(exchange);
                    } else sendNotFound(exchange);
                }
                default -> sendNotFound(exchange);
            }
        } catch (Exception e) {
            System.err.println("Ошибка EpicsHandler: " + e.getMessage());
            sendInternalError(exchange);
        }
    }
}
