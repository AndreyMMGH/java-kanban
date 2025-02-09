package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> NodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void addTask(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(NodeMap.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(task, tail, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        NodeMap.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node thisNode = head;
        while (thisNode != null) {
            tasks.add(thisNode.task);
            thisNode = thisNode.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        final Node next = node.next;
        final Node prev = node.prev;
        final int taskId = node.task.getId();

        if (head == node && tail == node) {
            head = null;
            tail = null;
        } else if (head == node) {
            head = next;
            if (head != null) {
                head.prev = null;
            }
        } else if (tail == node) {
            tail = prev;
            if (tail != null) {
                tail.next = null;
            }
        } else {
            prev.next = next;
            next.prev = prev;
        }

        NodeMap.remove(taskId);
    }

    private static class Node {
        Task task;
        Node prev;
        Node next;

        private Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }
}
