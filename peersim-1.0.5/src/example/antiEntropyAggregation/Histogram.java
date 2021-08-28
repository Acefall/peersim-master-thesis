package example.antiEntropyAggregation;

public class Histogram {
    private AntiEntropyCounter[] histogram;
    private int a = -1;

    private int K;
    private double m;
    private double M;

    public Histogram(int K, double m, double M) {
        this.K = K;
        this.m  = m;
        this.M = M;
        initialize(false);
    }

    public void initialize(boolean leader) {
        histogram = new AntiEntropyCounter[K];
        for (int k = 0; k < K; k++){
            histogram[k] = new AntiEntropyCounter(leader);
        }
    }

    public void incrementBin(int binIndex, int increment){
        histogram[binIndex].increment(increment);
    }

    public void decrementBin(int binIndex, int decrement){
        histogram[binIndex].decrement(decrement);
    }


    public Histogram clone() {
        Histogram cloned = new Histogram(K, m, M);
        for (int k = 0; k < K; k++) {
            cloned.histogram[k] = histogram[k].clone();
        }
        return cloned;
    }

    public double approximatedMean() {
        double weightedSum = 0;
        double n = 0;
        for (int k = 0; k < K; k++){
            double count = histogram[k].getCount();
            weightedSum += midPoint(k) * count;
            n += count;
        }
        if(n > 0){
            return weightedSum/n;
        }
        return 0;
    }


    private double binWidth(){
        return (M-m)/K;
    }

    private double binStart(int a){
        return m+a*binWidth();
    }

    private double binEnd(int a){
        return binStart(a)+binWidth();
    }

    private double midPoint(int a){
        return binStart(a)+binWidth()/2;
    }


    public int getBin(double x){
        int interval = (int) Math.floor(K*(x -m)/(M-m));
        interval = Math.max(0, interval);
        interval = Math.min(K-1, interval);
        return interval;
    }

    public void exchange(Histogram histogram2){
        for (int k = 0; k < K; k++) {
            histogram[k].exchange(histogram2.histogram[k]);
        }
    }
}
