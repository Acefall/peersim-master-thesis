package timeseries;

import com.sun.source.tree.Tree;
import peersim.config.Configuration;
import peersim.core.Network;
import timeseries.luftdaten.FCFSMapping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConstantDataProvider implements IDataProvider{
    private final IDataSource dataSource;
    private final FCFSMapping mapping;
    private final Duration validDuration;
    private final Double defaultNa;
    private final int n;

    private SortedMap<String, Observation> observations = new TreeMap<>();

    private static final String PAR_DATA_SOURCE = "dataSource";
    private static final String PAR_VALID_DURATION = "validDuration";
    private static final String PAR_DEFAULT_NA = "defaultNaN";


    public ConstantDataProvider(String name){
        this.mapping = new FCFSMapping();
        this.n = Network.size();
        dataSource = (IDataSource) Configuration.getInstance(name + "." + PAR_DATA_SOURCE);
        validDuration = Duration.ofMillis(Configuration.getInt(name + "." + PAR_VALID_DURATION));
        defaultNa = Configuration.getDouble(name + "." + PAR_DEFAULT_NA, Double.NaN);
    }

    private void getObservations(LocalDateTime t, Duration searchDuration) {
        if(observations.isEmpty()) {
            observations.putAll(dataSource.sensorValuesAt(t, searchDuration));
        }
    }

    @Override
    public HashMap<Integer, Double> nodeValuesAt(LocalDateTime t) {
        getObservations(t, validDuration);

        // Fill result with default values
        HashMap<Integer, Double> nodeValues = new HashMap<Integer, Double>();
        for (int i = 0; i < n; i++) {
            nodeValues.put(i, defaultNa);
        }

        // Put last observation into result if not too old
        for (var entry : observations.entrySet()) {
            Observation observation = entry.getValue();
            if(!Double.isNaN(observation.measurement)) {
                if (observation.t.isBefore(t.minus(validDuration))) {
                    nodeValues.put(mapping.sensorToNode(entry.getKey()), defaultNa);
                } else {
                    nodeValues.put(mapping.sensorToNode(entry.getKey()), observation.measurement);
                }
            }
        }

        return nodeValues;
    }
}
