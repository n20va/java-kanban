package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {

    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();

            if ("GET".equalsIgnoreCase(method)) {
                if (query == null) {
                    List<Subtask> subtasks = manager.getAllSubtasks();
                    sendText(exchange, gson.toJson(subtasks), 200);
                } else if (query.startsWith("id=")) {
                    int id = Integer.parseInt(query.split("=")[1]);
                    Subtask subtask = manager.getSubtaskById(id);
                    if (subtask == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    sendText(exchange, gson.toJson(subtask), 200);
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                Subtask subtask = gson.fromJson(isr, Subtask.class);
                isr.close();

                try {
                    if (subtask.getId() == 0) {
                        manager.addSubtask(subtask);
                    } else {
                        manager.updateSubtask(subtask);
                    }
                    sendText(exchange, "{\"status\":\"ok\"}", 201);
                } catch (TaskOverlapException e) {
                    sendHasOverlaps(exchange);
                }
            } else if ("DELETE".equalsIgnoreCase(method)) {
                if (query == null) {
                    manager.clearSubtasks();
                    sendText(exchange, "{\"status\":\"all subtasks deleted\"}", 200);
                } else if (query.startsWith("id=")) {
                    int id = Integer.parseInt(query.split("=")[1]);
                    try {
                        manager.removeSubtaskById(id);
                        sendText(exchange, "{\"status\":\"subtask deleted\"}", 200);
                    } catch (TaskNotFoundException e) {
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
