package protocols.adaptiveSampling;

public class MeanBin {
    private final int time;
    private int sampleCount;
    private double sum;
    private double absoluteTimeDiffSum;

    public MeanBin(int time) {
        this.time = time;
    }

    public void addSample(Sample sample){
        sampleCount++;
        sum += sample.getValue();
        absoluteTimeDiffSum += Math.abs(time - sample.getTime());
    }

    public double getMean(){
        return sum / sampleCount;
    }

    public double getWeight(){
        return sampleCount / (1 + (absoluteTimeDiffSum / sampleCount));
    }

    public int getSampleCount() {
        return sampleCount;
    }
}
