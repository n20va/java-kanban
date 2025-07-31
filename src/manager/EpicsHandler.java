package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Subtask;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            switch (method) {
                case "GET" -> handleGet(exchange, query, path);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDelete(exchange, query);
                default -> sendError(exchange, 405, "Method not allowed");
            }
        } catch (Exception e) {
            System.err.println("Ошибка EpicsHandler: " + e.getMessage());
            sendInternalError(exchange);
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String query, String path) {
        try {
            if (query != null && query.startsWith("id=")) {
                int id = Integer.parseInt(query.substring(3));
                Epic epic = manager.getEpicById(id);
                if (epic != null) {
                    sendText(exchange, gson.toJson(epic), 200);
                } else {
                    sendError(exchange, 404, "Epic not found");
                }

            } else if (path.contains("/subtasks") && query != null && query.startsWith("id=")) {
                int id = Integer.parseInt(query.substring(3));
                if (manager.getEpicById(id) == null) {
                    sendError(exchange, 404, "Epic not found");
                    return;
                }
                List<Subtask> subtasks = manager.getSubtasksOfEpic(id);
                sendText(exchange, gson.toJson(subtasks), 200);

            } else {
                sendText(exchange, gson.toJson(manager.getAllEpics()), 200);
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки GET EpicsHandler: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(body, Epic.class);

            if (epic.getId() == 0) {
                manager.addEpic(epic);
                sendText(exchange, "{\"id\":" + epic.getId() + "}", 201);
            } else if (manager.getEpicById(epic.getId()) != null) {
                manager.updateEpic(epic);
                sendText(exchange, gson.toJson(epic), 201);
            } else {
                sendError(exchange, 404, "Epic not found");
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки POST EpicsHandler: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String query) {
        try {
            if (query != null && query.startsWith("id=")) {
                int id = Integer.parseInt(query.substring(3));
                if (manager.getEpicById(id) != null) {
                    manager.removeEpicById(id);
                    sendText(exchange, "{\"status\":\"deleted\"}", 200);
                } else {
                    sendError(exchange, 404, "Epic not found");
                }
            } else {
                manager.clearEpics();
                sendText(exchange, "{\"status\":\"all epics deleted\"}", 200);
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки DELETE EpicsHandler: " + e.getMessage());
            sendInternalError(exchange);
        }
    }
}
