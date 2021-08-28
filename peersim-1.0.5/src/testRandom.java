import java.util.ArrayList;
import java.util.Random;

public class testRandom {

    public static void main(String[] args) {
        Random random = new Random();


        for (int j = 0; j < 1000; j++) {
            double sum = 0;
            double n = Math.floor(Math.pow(10, 6));
            for (int i = 0; i < n; i++) {
                sum += random.nextDouble();
            }
            System.out.println(sum/n);
        }


    }
}
