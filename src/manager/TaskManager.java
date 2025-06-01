package manager;

import model.Task;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);
    List<Task> getTasks();
}