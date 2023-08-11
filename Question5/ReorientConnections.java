import java.util.ArrayList;
import java.util.List;

public class ReorientConnections {
    public int minReorientConnections(int n, int[][] connections) {
        // Create the adjacency list to represent the tree
        List<Integer>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }
        for (int[] connection : connections) {
            int ai = connection[0];
            int bi = connection[1];
            graph[ai].add(bi);
        }

        // Perform DFS to count the edges that need to be reversed
        int[] reversedEdges = new int[1]; // Use an array to store the count as it will be modified within DFS
        boolean[] visited = new boolean[n];
        dfs(0, graph, visited, reversedEdges);

        return reversedEdges[0];
    }

    private void dfs(int node, List<Integer>[] graph, boolean[] visited, int[] reversedEdges) {
        visited[node] = true;
        for (int neighbor : graph[node]) {
            if (!visited[neighbor]) {
                reversedEdges[0]++;
                dfs(neighbor, graph, visited, reversedEdges);
            }
        }
    }

    public static void main(String[] args) {
        int n = 6;
        int[][] connections = {{0, 1}, {1, 3}, {2, 3}, {4, 0}, {4, 5}};

        ReorientConnections solution = new ReorientConnections();
        int result = solution.minReorientConnections(n, connections);
        System.out.println("Minimum edges to be reversed: " + result); // Output: 3
    }
}
