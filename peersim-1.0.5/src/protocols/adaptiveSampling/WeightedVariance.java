package protocols.adaptiveSampling;

import java.util.Set;

public final class WeightedVariance {
    private static double weightedMean(Set<MeanBin> meanBins){
        double weightedSum = 0;
        double totalWeight = 0;
        for (MeanBin meanBin : meanBins){
            weightedSum += meanBin.getMean() * meanBin.getWeight();
            totalWeight += meanBin.getWeight();
        }

        return weightedSum / (totalWeight * meanBins.size());
    }

    public static double getWeightedVariance(Set<MeanBin> meanBins){
        double weightedMean = weightedMean(meanBins);
        double squaredDifferences = 0;
        double totalWeight = 0;
        for (MeanBin meanBin : meanBins){
            squaredDifferences += Math.pow(meanBin.getMean() - weightedMean, 2) * meanBin.getWeight();
            totalWeight += meanBin.getWeight();
        }

        if(totalWeight * meanBins.size() == 0){
            return 0;
        }
        return squaredDifferences / (totalWeight * meanBins.size());
    }
}
