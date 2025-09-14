package com.smarthome.util;

import java.util.*;

/**
 * Represents an undirected graph structure.
 * Used for modeling network topology of devices in SmartHome.
 */
public class Graph {

    // Adjacency list representation
    private final Map<String, List<String>> adjacencyList = new HashMap<>();

    // ---------------- Nodes & Edges ----------------

    /** Adds a node to the graph if it doesn't exist already. */
    public void addNode(String node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }

    /** Adds an undirected edge between two nodes. */
    public void addEdge(String node1, String node2) {
        addNode(node1);
        addNode(node2);
        adjacencyList.get(node1).add(node2);
        adjacencyList.get(node2).add(node1);
    }

    /** Removes the edge between two nodes. */
    public void removeEdge(String node1, String node2) {
        if (adjacencyList.containsKey(node1)) adjacencyList.get(node1).remove(node2);
        if (adjacencyList.containsKey(node2)) adjacencyList.get(node2).remove(node1);
    }

    /** Removes a node and all its connections. */
    public void removeNode(String node) {
        if (!adjacencyList.containsKey(node)) return;
        for (String neighbor : adjacencyList.get(node)) {
            adjacencyList.get(neighbor).remove(node);
        }
        adjacencyList.remove(node);
    }

    // ---------------- BFS Traversal ----------------

    /**
     * Performs Breadth-First Search starting from the given node.
     * @param start the starting node
     * @return List of visited nodes in BFS order
     */
    public List<String> bfs(String start) {
        List<String> visited = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> seen = new HashSet<>();

        queue.add(start);
        seen.add(start);

        while (!queue.isEmpty()) {
            String node = queue.poll();
            visited.add(node);

            for (String neighbor : adjacencyList.getOrDefault(node, Collections.emptyList())) {
                if (!seen.contains(neighbor)) {
                    seen.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return visited;
    }

    // ---------------- Topology Display ----------------

    /**
     * Displays the graph as a tree starting from the root and also BFS order.
     * @param root Root node to start the display
     * @return Formatted topology string
     */
    public String showTopology(String root) {
        if (!adjacencyList.containsKey(root)) return root + " (root not found)";
        StringBuilder sb = new StringBuilder();
        Set<String> seen = new HashSet<>();
        displayTree(root, "", sb, seen);
        sb.append("\nBFS: ").append(String.join(" -> ", bfs(root)));
        return sb.toString();
    }

    /** Helper recursive method to print tree-like structure of the graph. */
    private void displayTree(String node, String prefix, StringBuilder sb, Set<String> seen) {
        if (seen.contains(node)) return;
        seen.add(node);
        sb.append(prefix).append(node).append("\n");

        List<String> neighbors = adjacencyList.getOrDefault(node, Collections.emptyList());
        for (int i = 0; i < neighbors.size(); i++) {
            String child = neighbors.get(i);
            if (seen.contains(child)) continue;
            String newPrefix = prefix + (i == neighbors.size() - 1 ? "└── " : "├── ");
            displayTree(child, newPrefix, sb, seen);
        }
    }

    // ---------------- Utility ----------------

    /** Returns all nodes in the graph. */
    public Set<String> getNodes() {
        return adjacencyList.keySet();
    }

    /** Returns neighbors of a given node. */
    public List<String> getNeighbors(String node) {
        return adjacencyList.getOrDefault(node, Collections.emptyList());
    }
}
