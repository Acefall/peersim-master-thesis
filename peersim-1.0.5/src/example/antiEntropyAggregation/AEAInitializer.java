package example.antiEntropyAggregation;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

public class AEAInitializer implements Control {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";

    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int protocolID;

    public AEAInitializer(String prefix) {
        protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
    }

    public boolean execute() {
        System.out.println("Executing AEAInitializer");
        RequiresLeader aeaProtocol = (RequiresLeader) Network.get(0).getProtocol(protocolID);
        aeaProtocol.designateAsLeader();
        return false;
    }
}
