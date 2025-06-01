package model;

public class Task {
    protected String name;
    protected String description;
    protected Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Status getStatus() { return status; }
}