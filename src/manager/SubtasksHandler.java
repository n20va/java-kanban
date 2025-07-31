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
            String query = exchange.getRequestURI().getQuery();

            switch (method) {
                case "GET" -> handleGet(exchange, query);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDelete(exchange, query);
                default -> sendError(exchange, 405, "Method not allowed");
            }
        } catch (Exception e) {
            System.err.println("Ошибка SubtasksHandler: " + e.getMessage());
            sendInternalError(exchange);
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String query) {
        try {
            if (query != null && query.startsWith("id=")) {
                int id = Integer.parseInt(query.substring(3));
                Subtask subtask = manager.getSubtaskById(id);
                if (subtask != null) {
                    sendText(exchange, gson.toJson(subtask), 200);
                } else {
                    sendError(exchange, 404, "Subtask not found");
                }
            } else {
                sendText(exchange, gson.toJson(manager.getAllSubtasks()), 200);
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки GET SubtasksHandler: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);

            try {
                if (subtask.getId() == 0) {
                    if (manager.getEpicById(subtask.getEpicId()) == null) {
                        sendError(exchange, 400, "Related epic not found");
                        return;
                    }
                    manager.addSubtask(subtask);
                    sendText(exchange, "{\"id\":" + subtask.getId() + "}", 201);

                } else {
                    Subtask old = manager.getSubtaskById(subtask.getId());
                    if (old == null) {
                        sendError(exchange, 404, "Subtask not found");
                    } else if (old.getEpicId() != subtask.getEpicId()) {
                        sendError(exchange, 400, "Cannot change epicId of subtask");
                    } else {
                        manager.updateSubtask(subtask);
                        sendText(exchange, gson.toJson(subtask), 201);
                    }
                }
            } catch (TaskOverlapException e) {
                sendError(exchange, 409, e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки POST SubtasksHandler: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String query) {
        try {
            if (query != null && query.startsWith("id=")) {
                int id = Integer.parseInt(query.substring(3));
                if (manager.getSubtaskById(id) != null) {
                    manager.removeSubtaskById(id);
                    sendText(exchange, "{\"status\":\"deleted\"}", 200);
                } else {
                    sendError(exchange, 404, "Subtask not found");
                }
            } else {
                manager.clearSubtasks();
                sendText(exchange, "{\"status\":\"all subtasks deleted\"}", 200);
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки DELETE SubtasksHandler: " + e.getMessage());
            sendInternalError(exchange);
        }
    }
}
