package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public TasksHandler(TaskManager manager) {
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
            System.err.println("Ошибка TasksHandler: " + e.getMessage());
            sendInternalError(exchange);
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String query) {
        try {
            if (query != null && query.startsWith("id=")) {
                int id = Integer.parseInt(query.substring(3));
                Task task = manager.getTaskById(id);
                if (task != null) {
                    sendText(exchange, gson.toJson(task), 200);
                } else {
                    sendError(exchange, 404, "Task not found");
                }
            } else {
                sendText(exchange, gson.toJson(manager.getAllTasks()), 200);
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки GET TasksHandler: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);

            try {
                if (task.getId() == 0) {
                    manager.addTask(task);
                    sendText(exchange, "{\"id\":" + task.getId() + "}", 201);
                } else if (manager.getTaskById(task.getId()) != null) {
                    manager.updateTask(task);
                    sendText(exchange, gson.toJson(task), 201);
                } else {
                    sendError(exchange, 404, "Task not found");
                }
            } catch (TaskOverlapException e) {
                sendError(exchange, 409, e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки POST TasksHandler: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String query) {
        try {
            if (query != null && query.startsWith("id=")) {
                int id = Integer.parseInt(query.substring(3));
                if (manager.getTaskById(id) != null) {
                    manager.removeTaskById(id);
                    sendText(exchange, "{\"status\":\"deleted\"}", 200);
                } else {
                    sendError(exchange, 404, "Task not found");
                }
            } else {
                manager.clearTasks();
                sendText(exchange, "{\"status\":\"all tasks deleted\"}", 200);
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки DELETE TasksHandler: " + e.getMessage());
            sendInternalError(exchange);
        }
    }
}
