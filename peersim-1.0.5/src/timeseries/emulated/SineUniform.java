// Author: Alexander Weinmann uni@aweinmann.de
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

/**
 * Synthetic data source where all values of the sensors follow a sine wave. Each node u has an offset r_u which is
 * uniformly sampled from [-1, 1]. Then the output of a node u is sin(t*2*PI/period)+r_u.
 * Values are always valid.
 */

public class SineUniform implements IDataSource {
    /**
     * Name of the config parameter that specifies the period
     */
    private static final String PARAMETER_PERIOD = "period";
    private static final String PAR_SEED = "seed";


    private final int n;
    private final double period;
    HashMap<String, Double> offsets = new HashMap<>();
    private Random random = new Random();


    public SineUniform(double period, int n, int seed) {
        this.period = period;
        this.n = n;
        random.setSeed(seed);

        for (int i = 0; i < Network.size(); ++i) {
            double randomDouble = -1 + 2 * random.nextDouble();
            offsets.put(Integer.toString(i), randomDouble);
        }
    }

    public SineUniform(String name) {
        period = Configuration.getDouble(name + "." + PARAMETER_PERIOD);
        n = Network.size();
        random.setSeed(Configuration.getInt(name + "." + PAR_SEED));

        for (int i = 0; i < Network.size(); ++i) {
            double randomDouble = -1 + 2 * random.nextDouble();
            offsets.put(Integer.toString(i), randomDouble);
        }
    }


    @Override
    public Map<String, Observation> sensorValuesAt(LocalDateTime t, Duration validDuration) {
        HashMap<String, Observation> result = new HashMap<String, Observation>();
        for (int i = 0; i < n; i++) {
            result.put(Integer.toString(i), new Observation(t, Math.sin(CommonState.getTime() * 2 * Math.PI / period) +
                    offsets.get(Integer.toString(i))));
        }
        return result;
    }
}