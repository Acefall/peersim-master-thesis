package timeseries.emulated;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import timeseries.IDataSource;
import timeseries.Observation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class MultiplicativeNoiseSine implements IDataSource {
    private static final String PAR_AMPLITUDE = "amplitude";
    private static final String PAR_OFFSET_Y = "offsetY";
    private static final String PAR_NOISE_RATIO = "noiseRatio";
    private static final String PAR_TIME_LENGTH_IN_ROUNDS = "timeLengthInRounds";
    private static final String PAR_PERIOD = "period";


    private final int n;
    private final int period;
    private final double amplitude;
    private final double noiseRatio;
    private final double offsetY;
    private final int timeLengthInRounds;
    HashMap<String, Observation> data = new HashMap<>();

    public MultiplicativeNoiseSine(String name) {
        n = Network.size();
        amplitude = Configuration.getDouble(name + "." + PAR_AMPLITUDE);
        noiseRatio = Configuration.getDouble(name + "." + PAR_NOISE_RATIO);
        offsetY = Configuration.getDouble(name + "." + PAR_OFFSET_Y);
        timeLengthInRounds = Configuration.getInt(name + "." + PAR_TIME_LENGTH_IN_ROUNDS);
        period = Configuration.getInt(name + "." + PAR_PERIOD);
    }

    @Override
    public HashMap<String, Observation> sensorValuesAt(LocalDateTime t, Duration validDuration) {
        if((CommonState.getIntTime() % timeLengthInRounds) == 0) {
            double min, max;
            double time = CommonState.getIntTime() / ((float) timeLengthInRounds);
            double mean = amplitude * Math.sin(2*Math.PI*time/period) + offsetY;

            min = mean * (1+noiseRatio);
            max = mean * (1-noiseRatio);

            for (int i = 0; i < n; ++i) {
                double randomDouble = min + (max - min) * CommonState.r.nextDouble();
                data.put(Integer.toString(i), new Observation(t, randomDouble));
            }
        }
        return data;
    }
}
