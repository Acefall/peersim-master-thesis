package example.pushAndPullAggregation;

import example.AggregationProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

public class RandomUniformInitializer implements Control {
    /**
     * The upper bound of the values.
     */
    private static final String PAR_MAX = "max";

    /**
     * The lower bound of the values. Defaults to -max.
     */
    private static final String PAR_MIN = "min";


    private static final String PAR_PUSH_PROT = "pushProtocol";
    private static final String PAR_PULL_PROT = "pullProtocol";


    /** Maximum interval value,
     obtained from config property {@link #PAR_MAX}. */
    private final double max;

    /** Minimum interval value,
     obtained from config property {@link #PAR_MIN}. */
    private final double min;

    /** Protocol identifier, obtained from config property {@link #PAR_PUSH_PROT}. */
    private final int pushProtocolID;

    /** Protocol identifier, obtained from config property {@link #PAR_PULL_PROT}. */
    private final int pullProtocolID;

    public RandomUniformInitializer(String prefix) {
        max = Configuration.getDouble(prefix + "." + PAR_MAX);
        min = Configuration.getDouble(prefix + "." + PAR_MIN, -max);
        pushProtocolID = Configuration.getPid(prefix + "." + PAR_PUSH_PROT);
        pullProtocolID = Configuration.getPid(prefix + "." + PAR_PULL_PROT);
    }

    public boolean execute() {
        for (int i = 0; i < Network.size(); ++i) {
            double randomDouble = min + (max - min) * CommonState.r.nextDouble();
            ((AggregationProtocol) Network.get(i).getProtocol(pushProtocolID)).setInput(randomDouble);
            ((AggregationProtocol) Network.get(i).getProtocol(pullProtocolID)).setInput(randomDouble);
        }
        return false;
    }
}
