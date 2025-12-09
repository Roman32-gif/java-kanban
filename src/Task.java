public class Task {

    private int id;
    private final String name;
    private final String description;
    private Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public void setStatus (Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
