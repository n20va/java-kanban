package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Task> prioritized = manager.getPrioritizedTasks();
                sendText(exchange, gson.toJson(prioritized), 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}

