package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import java.nio.charset.StandardCharsets;
import java.util.List;


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

            switch (method) {
                case "GET" -> {
                    List<Task> tasks = manager.getAllTasks();
                    sendText(exchange, gson.toJson(tasks), 200);
                }
                case "POST" -> {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);

                    if (task.getId() == 0) {
                        manager.addTask(task);
                        sendText(exchange, "{\"id\":" + task.getId() + "}", 201);
                    } else if (manager.getTaskById(task.getId()) != null) {
                        manager.updateTask(task);
                        sendText(exchange, gson.toJson(task), 201);
                    } else {
                        sendNotFound(exchange);
                    }
                }
                case "DELETE" -> {
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.startsWith("id=")) {
                        int id = Integer.parseInt(query.substring(3));
                        if (manager.getTaskById(id) != null) {
                            manager.removeTaskById(id);
                            sendText(exchange, "{\"status\":\"deleted\"}", 201);
                        } else sendNotFound(exchange);
                    } else sendNotFound(exchange);
                }
                default -> sendNotFound(exchange);
            }
        } catch (Exception e) {
            System.err.println("Ошибка TasksHandler: " + e.getMessage());

            sendInternalError(exchange);
        }
    }
}
