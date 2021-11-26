package timeseries.emulated;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import timeseries.IDataSource;
import timeseries.Observation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class MultiplicativeNoisePeriodic implements IDataSource {
    private static final String PAR_MEAN_1 = "mean1";
    private static final String PAR_MEAN_2 = "mean2";
    private static final String PAR_NOISE_RATIO = "noiseRatio";
    private static final String PAR_TIME_LENGTH_IN_ROUNDS = "timeLengthInRounds";
    private static final String PAR_PERIOD = "period";


    private final int n;
    private final int period;
    private final double mean1;
    private final double mean2;
    private final double noiseRatio;
    private final int timeLengthInRounds;
    HashMap<String, Observation> data = new HashMap<>();

    public MultiplicativeNoisePeriodic(String name) {
        n = Network.size();
        mean1 = Configuration.getDouble(name + "." + PAR_MEAN_1);
        mean2 = Configuration.getDouble(name + "." + PAR_MEAN_2);
        noiseRatio = Configuration.getDouble(name + "." + PAR_NOISE_RATIO);
        timeLengthInRounds = Configuration.getInt(name + "." + PAR_TIME_LENGTH_IN_ROUNDS);
        period = Configuration.getInt(name + "." + PAR_PERIOD);
    }

    @Override
    public HashMap<String, Observation> sensorValuesAt(LocalDateTime t, Duration validDuration) {
        if((CommonState.getIntTime() % timeLengthInRounds) == 0) {
            double min, max;
            if(Math.floor(CommonState.getIntTime() / ((float) (timeLengthInRounds * period))) % 2 == 0){
                min = mean1 * (1-noiseRatio);
                max = mean1 * (1+noiseRatio);
            }else{
                min = mean2 * (1-noiseRatio);
                max = mean2 * (1+noiseRatio);
            }
            for (int i = 0; i < n; ++i) {
                double randomDouble = min + (max - min) * CommonState.r.nextDouble();
                data.put(Integer.toString(i), new Observation(t, randomDouble));
            }
        }
        return data;
    }
}
