import java.util.ArrayList;
import java.util.List;

class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    @Override
    public String toString() {
        return String.format("Epic{id=%d, title='%s', description='%s', status=%s, subtasks=%s}",
                getId(), getTitle(), getDescription(), getStatus(), subtaskIds);
    }
}
