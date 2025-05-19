class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("Subtask{id=%d, title='%s', description='%s', status=%s, epicId=%d}",
                getId(), getTitle(), getDescription(), getStatus(), epicId);
    }
}