package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {

    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();

            if ("GET".equalsIgnoreCase(method)) {
                if (query == null) {
                    List<Epic> epics = manager.getAllEpics();
                    sendText(exchange, gson.toJson(epics), 200);
                } else if (query.startsWith("id=")) {
                    int id = Integer.parseInt(query.split("=")[1]);
                    Epic epic = manager.getEpicById(id);
                    if (epic == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    sendText(exchange, gson.toJson(epic), 200);
                }
            } else if ("DELETE".equalsIgnoreCase(method)) {
                if (query == null) {
                    manager.clearEpics();
                    sendText(exchange, "{\"status\":\"all epics deleted\"}", 200);
                } else if (query.startsWith("id=")) {
                    int id = Integer.parseInt(query.split("=")[1]);
                    try {
                        manager.removeEpicById(id);
                        sendText(exchange, "{\"status\":\"epic deleted\"}", 200);
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
