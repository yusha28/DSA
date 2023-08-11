public class MinimumCostOfClothing {
    public static int findMinimumCost(int[][] price) {
        int N = price.length;
        if (N != 3) {
            throw new IllegalArgumentException("The number of people (N) must be 3.");
        }

        int minCost1 = price[0][0];
        int minCost2 = price[0][1];
        int minCost3 = price[0][2];

        for (int i = 1; i < N; i++) {
            int prevMinCost1 = minCost1;
            int prevMinCost2 = minCost2;
            int prevMinCost3 = minCost3;

            minCost1 = price[i][0] + Math.min(prevMinCost2, prevMinCost3);
            minCost2 = price[i][1] + Math.min(prevMinCost1, prevMinCost3);
            minCost3 = price[i][2] + Math.min(prevMinCost1, prevMinCost2);
        }

        return Math.min(minCost1, Math.min(minCost2, minCost3));
    }

    public static void main(String[] args) {
        int[][] price = {
            {14, 4, 11},
            {11, 14, 3},
            {14, 2, 10}
        };
        int result = findMinimumCost(price);
        System.out.println("Minimum cost required: " + result); // Output: 9
    }
}
