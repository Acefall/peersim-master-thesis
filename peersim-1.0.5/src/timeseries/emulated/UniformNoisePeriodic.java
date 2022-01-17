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

public class UniformNoisePeriodic implements IDataSource {
    private static final String PAR_MIN_1 = "min1";
    private static final String PAR_MAX_1 = "max1";
    private static final String PAR_MIN_2 = "min2";
    private static final String PAR_MAX_2 = "max2";
    private static final String PAR_TIME_LENGTH_IN_ROUNDS = "timeLengthInRounds";
    private static final String PAR_PERIOD = "period";


    private final int n;
    private final int period;
    private final double min1;
    private final double max1;
    private final double min2;
    private final double max2;
    private final int timeLengthInRounds;
    HashMap<String, Observation> data = new HashMap<>();

    public UniformNoisePeriodic(String name) {
        n = Network.size();
        min1 = Configuration.getDouble(name + "." + PAR_MIN_1);
        max1 = Configuration.getDouble(name + "." + PAR_MAX_1);
        min2 = Configuration.getDouble(name + "." + PAR_MIN_2);
        max2 = Configuration.getDouble(name + "." + PAR_MAX_2);
        timeLengthInRounds = Configuration.getInt(name + "." + PAR_TIME_LENGTH_IN_ROUNDS);
        period = Configuration.getInt(name + "." + PAR_PERIOD);
    }

    @Override
    public Map<String, Observation> sensorValuesAt(LocalDateTime t, Duration validDuration) {
        if((CommonState.getIntTime() % timeLengthInRounds) == 0) {
            double min, max;
            if(Math.floor(CommonState.getIntTime() / ((float) (timeLengthInRounds * period))) % 2 == 0){
                min = min1;
                max = max1;
            }else{
                min = min2;
                max = max2;
            }
            for (int i = 0; i < n; ++i) {
                double randomDouble = min + (max - min) * CommonState.r.nextDouble();
                data.put(Integer.toString(i), new Observation(t, randomDouble));
            }
        }
        return data;
    }
}
