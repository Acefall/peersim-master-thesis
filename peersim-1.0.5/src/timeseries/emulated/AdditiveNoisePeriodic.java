package timeseries.emulated;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import timeseries.IDataSource;
import timeseries.Observation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AdditiveNoisePeriodic implements IDataSource {
    private static final String PAR_MEAN_1 = "mean1";
    private static final String PAR_MEAN_2 = "mean2";
    private static final String PAR_RANGE = "range";
    private static final String PAR_TIME_LENGTH_IN_ROUNDS = "timeLengthInRounds";
    private static final String PAR_PERIOD = "period";
    private static final String PAR_SEED = "seed";


    private final int n;
    private final int period;
    private final double mean1;
    private final double mean2;
    private final double range;
    private final int timeLengthInRounds;
    HashMap<String, Observation> data = new HashMap<>();
    private Random random = new Random();

    public AdditiveNoisePeriodic(String name) {
        n = Network.size();
        mean1 = Configuration.getDouble(name + "." + PAR_MEAN_1);
        mean2 = Configuration.getDouble(name + "." + PAR_MEAN_2);
        range = Configuration.getDouble(name + "." + PAR_RANGE);
        timeLengthInRounds = Configuration.getInt(name + "." + PAR_TIME_LENGTH_IN_ROUNDS);
        period = Configuration.getInt(name + "." + PAR_PERIOD);
        random.setSeed(Configuration.getInt(name + "." + PAR_SEED));
    }

    @Override
    public Map<String, Observation> sensorValuesAt(LocalDateTime t, Duration validDuration) {
        if((CommonState.getIntTime() % timeLengthInRounds) == 0) {
            double min, max;
            if(Math.floor(CommonState.getIntTime() / ((float) (timeLengthInRounds * period/2))) % 2 == 0){
                min = mean1 - range/2;
                max = mean1 + range/2;
            }else{
                min = mean2 - range/2;
                max = mean2 + range/2;
            }
            for (int i = 0; i < n; ++i) {
                double randomDouble = min + (max - min) * random.nextDouble();
                data.put(Integer.toString(i), new Observation(t, randomDouble));
            }
        }
        return data;
    }
}
