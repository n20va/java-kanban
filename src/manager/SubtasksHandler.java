package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Subtask;

import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET" -> sendText(exchange, gson.toJson(manager.getAllSubtasks()), 200);

                case "POST" -> {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(body, Subtask.class);

                    if (subtask.getId() == 0) {
                        if (manager.getEpicById(subtask.getEpicId()) == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        manager.addSubtask(subtask);
                        sendText(exchange, "{\"id\":" + subtask.getId() + "}", 201);

                    } else {
                        Subtask old = manager.getSubtaskById(subtask.getId());
                        if (old == null) {
                            sendNotFound(exchange);
                        } else if (old.getEpicId() != subtask.getEpicId()) {
                            sendHasOverlaps(exchange);
                        } else {
                            manager.updateSubtask(subtask);
                            sendText(exchange, gson.toJson(subtask), 201);
                        }
                    }
                }

                case "DELETE" -> {
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.startsWith("id=")) {
                        int id = Integer.parseInt(query.substring(3));
                        if (manager.getSubtaskById(id) != null) {
                            manager.removeSubtaskById(id);
                            sendText(exchange, "{\"status\":\"deleted\"}", 201);
                        } else sendNotFound(exchange);
                    } else sendNotFound(exchange);
                }

                default -> sendNotFound(exchange);
            }
        } catch (Exception e) {
            System.err.println("Ошибка SubtasksHandler: " + e.getMessage());
            sendInternalError(exchange);
        }
    }
}
