package example.loadBalance2;

import org.nfunk.jep.function.Str;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalStats;
import peersim.vector.SingleValue;

import java.util.Stack;

public class LBObserver2 implements Control {
    // ------------------------------------------------------------------------
    // Parameters
    // ------------------------------------------------------------------------

    /**
     * The protocol to operate on.
     *
     * @config
     */
    private static final String PAR_PROT = "protocol";

    /**
     * If defined, print the load value. Not defined by default.
     *
     * @config
     */
    private static final String PAR_SHOW_VALUES = "show_values";

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    /**
     * The name of this observer in the configuration file. Initialized by the
     * constructor parameter.
     */
    private final String name;

    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int pid;

    /**
     * Flag to show or not the load values at each node; obtained from config
     * property {@link #PAR_SHOW_VALUES}.
     */
    private boolean show_values;

    /**
     * This object keeps track of the values injected and produces statistics.
     * More details in: {@link peersim.util.IncrementalStats} .
     */
    private IncrementalStats stats = null;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Standard constructor that reads the configuration parameters. Invoked by
     * the simulation engine.
     *
     * @param name
     *            the configuration prefix identifier for this class.
     */
    public LBObserver2(String name){
        this.name = name;
        pid = Configuration.getPid(name + "." + PAR_PROT);
        show_values = Configuration.contains(name + "." + PAR_SHOW_VALUES);
        stats = new IncrementalStats();
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------


    @Override
    public boolean execute() {
        StringBuffer buf = new StringBuffer();
        int count_zero = 0;
        if (show_values) {
            buf.append(name + ": ");
        }

        final int len = Network.size();
        for (int i = 0; i < len; i++) {
            SingleValue prot = (SingleValue) Network.get(i).getProtocol(pid);
            double value = prot.getValue();
            stats.add(value);

            if (value == 0) {
                count_zero++;
            }

            if (show_values) {
                buf.append(value + ":");
            }
        }
        if (show_values) {
            System.out.println(buf.toString());
        }
        System.out.println(name + ":" + CommonState.getTime() + " " + stats.getAverage() + " " + stats.getMax() + " " + stats.getMin() + " " + count_zero + " " + stats.getVar());
        stats.reset();
        return false;
    }
}
