package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {

    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            if ("GET".equalsIgnoreCase(method)) {
                if (query == null) {
                    List<Task> tasks = manager.getAllTasks();
                    String json = gson.toJson(tasks);
                    sendText(exchange, json, 200);
                } else {
                    String[] parts = query.split("=");
                    if (parts.length == 2 && "id".equals(parts[0])) {
                        int id = Integer.parseInt(parts[1]);
                        Task task = manager.getTaskById(id);
                        if (task == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        String json = gson.toJson(task);
                        sendText(exchange, json, 200);
                    } else {
                        sendNotFound(exchange);
                    }
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(isr, Task.class);
                isr.close();

                try {
                    if (task.getId() == 0) {
                        manager.addTask(task);
                    } else {
                        manager.updateTask(task);
                    }
                    sendText(exchange, "{\"status\":\"ok\"}", 201);
                } catch (TaskOverlapException e) {
                    sendHasOverlaps(exchange);
                }
            } else if ("DELETE".equalsIgnoreCase(method)) {
                if (query == null) {
                    manager.clearTasks();
                    sendText(exchange, "{\"status\":\"all tasks deleted\"}", 200);
                } else {
                    String[] parts = query.split("=");
                    if (parts.length == 2 && "id".equals(parts[0])) {
                        int id = Integer.parseInt(parts[1]);
                        try {
                            manager.removeTaskById(id);
                            sendText(exchange, "{\"status\":\"task deleted\"}", 200);
                        } catch (TaskNotFoundException e) {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }
}
