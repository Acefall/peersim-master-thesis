package timeseries.emulated;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import timeseries.IDataSource;
import timeseries.Observation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class UniformNoise implements IDataSource {
    private static final String PAR_MIN = "min";
    private static final String PAR_MAX = "max";
    private static final String PAR_TIME_LENGTH_IN_ROUNDS = "timeLengthInRounds";


    private final int n;
    private final double min;
    private final double max;
    private final int timeLengthInRounds;
    HashMap<String, Observation> data = new HashMap<>();

    public UniformNoise(String name) {
        n = Network.size();
        min = Configuration.getDouble(name + "." + PAR_MIN);
        max = Configuration.getDouble(name + "." + PAR_MAX);
        timeLengthInRounds = Configuration.getInt(name + "." + PAR_TIME_LENGTH_IN_ROUNDS);
    }

    @Override
    public HashMap<String, Observation> sensorValuesAt(LocalDateTime t, Duration validDuration) {
        if(CommonState.getIntTime() % timeLengthInRounds == 0) {
            for (int i = 0; i < n; ++i) {
                double randomDouble = min + (max - min) * CommonState.r.nextDouble();
                data.put(Integer.toString(i), new Observation(t, randomDouble));
            }
        }
        return data;
    }
}
