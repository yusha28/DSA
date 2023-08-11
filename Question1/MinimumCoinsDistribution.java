public class MinimumCoinsDistribution {
    public static int minCoins(int[] ratings) {
        int n = ratings.length;
        int[] coins = new int[n];
        coins[0] = 1;

        // Traverse from left to right
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                coins[i] = coins[i - 1] + 1;
            } else {
                coins[i] = 1;
            }
        }

        // Traverse from right to left and update coins if necessary
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1] && coins[i] <= coins[i + 1]) {
                coins[i] = coins[i + 1] + 1;
            }
        }

        // Calculate the sum of coins assigned to all riders
        int sum = 0;
        for (int coin : coins) {
            sum += coin;
        }

        return sum;
    }

    public static void main(String[] args) {
        int[] ratings = {1, 0, 2};
        int result = minCoins(ratings);
        System.out.println("Minimum coins required: " + result); // Output: 5
    }
}
