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

/**
 * This class provides emulated data that is generated by a sine function.
 * The value at time t is t*sin(t*(2*pi)/period)
 */

public class Sine implements IDataSource {
    /**
     * Name of the config parameter that specifies the period
     */
    private static final String PARAMETER_PERIOD = "period";


    private final int n;
    private final double period;


    public Sine(String name) {
        period = Configuration.getDouble(name + "." + PARAMETER_PERIOD);
        n = Network.size();

    }


    @Override
    public Map<String, Observation> sensorValuesAt(LocalDateTime t, Duration validDuration) {
        HashMap<String, Observation> result = new HashMap<String, Observation>();
        for (int i = 0; i < n; i++) {
            result.put(Integer.toString(i), new Observation(t, Math.sin(CommonState.getTime() * 2 * Math.PI / period)));
        }
        return result;
    }
}
