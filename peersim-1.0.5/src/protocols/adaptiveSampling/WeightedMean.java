package protocols.adaptiveSampling;

public class WeightedMean {
    private double weightedSum;
    private double totalWeight;

    public void addValue(double value, double weight){
        weightedSum += value;
        totalWeight += weight;
    }

    public double getWeightedMean(){
        return  weightedSum / totalWeight;
    }
}
