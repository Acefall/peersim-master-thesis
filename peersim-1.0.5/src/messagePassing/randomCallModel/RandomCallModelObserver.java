package messagePassing.randomCallModel;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

/**
 * Generates a new mapping from every node to another node in every round.
 * */
public class RandomCallModelObserver implements Control {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";

    /**
     * Protocol identifier, obtained from config property {@link #PAR_PROT}.
     */
    private final int protocolID;

    public RandomCallModelObserver(String prefix) {
        protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
    }

    public boolean execute() {
        RandomCallModel protocol = (RandomCallModel) Network.get(0).getProtocol(protocolID);
        protocol.generateMapping();
        return false;
    }
}
