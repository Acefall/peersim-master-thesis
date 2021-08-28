package example.loadBalance2;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalStats;

public class QuotaObserver2 implements Control {
    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------
    /** The protocol to operate on*/
    private static final String PAR_PROT = "protocol";

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    /** The name of the observer in the configuration file*/
    private final String name;

    /** Protocol identifier*/
    private final  int pid;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------
    public QuotaObserver2(String name){
        this.name = name;
        pid = Configuration.getPid(name + "." + PAR_PROT);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    public boolean execute(){
        IncrementalStats stats = new IncrementalStats();
        for (int i = 0; i < Network.size(); i++) {
            BasicBalance2 protocol = (BasicBalance2) Network.get(i).getProtocol(pid);
            stats.add(protocol.quota);
        }

        /* Printing statistics */
        System.out.println(name + ": " + stats);
        return false;
    }
}
