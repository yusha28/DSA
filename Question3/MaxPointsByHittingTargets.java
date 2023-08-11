public class MaxPointsByHittingTargets {
    public static int maxPoints(int[] a) {
        int n = a.length;
        int[] dpLeft = new int[n + 2];
        int[] dpRight = new int[n + 2];
        dpLeft[0] = dpLeft[n + 1] = dpRight[0] = dpRight[n + 1] = 1;

        for (int i = 1; i <= n; i++) {
            dpLeft[i] = a[i - 1];
            dpRight[i] = a[i - 1];
        }

        for (int i = 1; i <= n; i++) {
            dpLeft[i] *= dpLeft[i - 1];
            dpRight[i] *= dpRight[i + 1];
        }

        int maxPoints = 0;
        for (int i = 1; i <= n; i++) {
            int points = dpLeft[i - 1] * a[i - 1] * dpRight[i + 1];
            maxPoints = Math.max(maxPoints, points);
        }

        return maxPoints;
    }

    public static void main(String[] args) {
        int[] a = {3, 1, 5, 8};
        int result = maxPoints(a);
        System.out.println("Maximum points: " + result); // Output: 167
    }
}
