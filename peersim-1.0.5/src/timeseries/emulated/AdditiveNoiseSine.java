package timeseries.emulated;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import timeseries.IDataSource;
import timeseries.Observation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Random;

public class AdditiveNoiseSine implements IDataSource {
    private static final String PAR_AMPLITUDE = "amplitude";
    private static final String PAR_OFFSET_Y = "offsetY";
    private static final String PAR_RANGE = "range";
    private static final String PAR_TIME_LENGTH_IN_ROUNDS = "timeLengthInRounds";
    private static final String PAR_PERIOD = "period";
    private static final String PAR_SEED = "seed";


    private final int n;
    private final int period;
    private final double amplitude;
    private final double range;
    private final double offsetY;
    private final int timeLengthInRounds;
    HashMap<String, Observation> data = new HashMap<>();
    private Random random = new Random();

    public AdditiveNoiseSine(String name) {
        n = Network.size();
        amplitude = Configuration.getDouble(name + "." + PAR_AMPLITUDE);
        range = Configuration.getDouble(name + "." + PAR_RANGE);
        offsetY = Configuration.getDouble(name + "." + PAR_OFFSET_Y);
        timeLengthInRounds = Configuration.getInt(name + "." + PAR_TIME_LENGTH_IN_ROUNDS);
        period = Configuration.getInt(name + "." + PAR_PERIOD);
        random.setSeed(Configuration.getInt(name + "." + PAR_SEED));
    }

    @Override
    public HashMap<String, Observation> sensorValuesAt(LocalDateTime t, Duration validDuration) {
        if((CommonState.getIntTime() % timeLengthInRounds) == 0) {
            double min, max;
            double time = CommonState.getIntTime() / ((float) timeLengthInRounds);
            double mean = amplitude * Math.sin(2*Math.PI*time/period) + offsetY;

            min = mean + range/2;
            max = mean - range/2;

            for (int i = 0; i < n; ++i) {
                double randomDouble = min + (max - min) * random.nextDouble();
                data.put(Integer.toString(i), new Observation(t, randomDouble));
            }
        }
        return data;
    }
}
