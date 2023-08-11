import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomPortPicker {
    private Set<Integer> whitelist;
    private Random random;

    public RandomPortPicker(int k, int[] blacklisted_ports) {
        whitelist = new HashSet<>();
        for (int i = 0; i < k; i++) {
            whitelist.add(i);
        }

        for (int port : blacklisted_ports) {
            whitelist.remove(port);
        }

        random = new Random();
    }

    public int get() {
        int randomIndex = random.nextInt(whitelist.size());
        int randomPort = 0;
        for (int port : whitelist) {
            if (randomIndex == 0) {
                randomPort = port;
                break;
            }
            randomIndex--;
        }
        return randomPort;
    }

    public static void main(String[] args) {
        int[] blacklisted_ports = {2, 3, 5};
        RandomPortPicker picker = new RandomPortPicker(7, blacklisted_ports);
        System.out.println(picker.get()); // Output: 0
        System.out.println(picker.get()); // Output: 4
    }
}
