import java.util.*;

public class MinStepsToCompleteTasks {
    public int minSteps(int N, int[][] prerequisites) {
        List<Integer>[] graph = new ArrayList[N + 1];
        for (int i = 1; i <= N; i++) {
            graph[i] = new ArrayList<>();
        }

        int[] inDegree = new int[N + 1];
        for (int[] prerequisite : prerequisites) {
            int x = prerequisite[0];
            int y = prerequisite[1];
            graph[x].add(y);
            inDegree[y]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 1; i <= N; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        int steps = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int curr = queue.poll();
                for (int neighbor : graph[curr]) {
                    inDegree[neighbor]--;
                    if (inDegree[neighbor] == 0) {
                        queue.add(neighbor);
                    }
                }
            }
            steps++;
        }

        return steps == N ? steps : -1;
    }

    public static void main(String[] args) {
        MinStepsToCompleteTasks minSteps = new MinStepsToCompleteTasks();
        int N = 3;
        int[][] prerequisites = {{1, 3}, {2, 3}};
        int result = minSteps.minSteps(N, prerequisites);
        System.out.println("Minimum steps needed: " + result); // Output: 2
    }
}
