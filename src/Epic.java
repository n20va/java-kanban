import java.util.ArrayList;
import java.util.List;

class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return String.format("Epic{id=%d, title='%s', description='%s', status=%s, subtasks=%s}",
                getId(), getTitle(), getDescription(), getStatus(), subtaskIds);
    }
}
