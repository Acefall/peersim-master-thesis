package protocols.adaptiveSampling;

public class Sample {
    private final int time;
    private final double value;

    public Sample(int time, double value) {
        this.time = time;
        this.value = value;
    }

    public int getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }
}
