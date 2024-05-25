package main;
import java.util.*;


public class MyGraph {
    HashMap<String, HashSet<String>> graph;

    MyGraph() {
        graph = new HashMap<>();
    }

    public void addVertex(String key) {
        graph.put(key, new HashSet<>(Set.of(key)));
    }

    public void addEdge(String from, String to) {
        graph.get(from).add(to);
    }

    public HashSet<String> getNeighbors(String vertex) {
        return graph.get(vertex);
    }

    public TreeSet<String> dfs(TreeSet<String> start) {
        TreeSet<String> marked = new TreeSet<>();
        TreeSet<String> visited = new TreeSet<>();
        for (String word: start) {
            if (!marked.contains(word)) {
                dfsRecursive(word, visited, marked);
            }
        }
        return visited;
    }

    private void dfsRecursive(String vertex, TreeSet<String> visited, TreeSet<String> color) {
        color.add(vertex);
        visited.add(vertex);
        if (getNeighbors(vertex) != null) {
            for (String neighbor : getNeighbors(vertex)) {
                if (!color.contains(neighbor)) {
                    dfsRecursive(neighbor, visited, color);
                }
            }
        }
    }
}
