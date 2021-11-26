package messagePassing.randomCallModel;

import java.util.*;

/**
 * Generates a random matching between integers. Every matching is equally likely.
 * */
public class RandomMatching {
    private final HashMap<Integer, Integer> matching = new HashMap<>();
    private final Random random;

    public RandomMatching(Random random){
        this.random = random;
    }

    public void generateMatching(int n) {
        // New list that contains all numbers from 0 to n(exclusive)
        List<Integer> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(i);
        }

        // Randomize it
        Collections.shuffle(list, random);

        // Match two consecutive elements of the list
        for (int i = 0; i < n; i+=2) {
            Integer a = list.get(i);
            Integer b = list.get(i+1);

            matching.put(a, b);
            matching.put(b, a);
        }
    }

    public int get(int nodeIndex){
        return matching.get(nodeIndex);
    }
}
