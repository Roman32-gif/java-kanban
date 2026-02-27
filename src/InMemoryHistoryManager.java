import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node tail;
    private Node head;

    @Override
    public void add(Task task) {
        Node copyTask = history.get(task.getId());
        if (copyTask != null) {
            removeNode(copyTask);
        }
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node node = history.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    private void linkLast(Task task) {
        Node node = new Node(task);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.setNext(node); //текущий последний узел это теперь наш новый узел
            node.setPrev(tail); // новый узел запоминает, что его предыдущий узел это старый tail
            tail = node; // хвост наш новый узел
        }
        history.put(task.getId(), node);
    }

    private void removeNode(Node node) {
        Node prev = node.getPrev();
        Node next = node.getNext();

        if (prev == null) { //если предыдущий узел 0, то голова это следующий узел
            head = next;
        } else { // предыдущий узел теперь следующий узел
            prev.setNext(next);
        }

        if (next == null) { // если следующий узел 0, то хвост теперь равен предыдущему узлу
            tail = prev;
        } else { // следущий теперь предыдущий
            next.setPrev(prev);
        }
    }

    private List<Task> getTasks() {
        List<Task> watchedTasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            watchedTasks.add(current.getTask());
            current = current.getNext();
        }
        return watchedTasks;
    }
}
