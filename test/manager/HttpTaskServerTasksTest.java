package test.manager;

import com.google.gson.Gson;
import model.Status;
import model.Task;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import manager.TaskManager;
import manager.*;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTasksTest {

    private TaskManager manager;
    private HttpTaskServer server;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Check HTTP", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());

        String json = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Должен вернуться код 201");
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test", tasks.get(0).getTitle());
    }
}
