package protocols.pullSum;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

/**
 * Logs the difference between the mass of all noes from the last round
 * to the mass of all noes from the current round.
 * */
public class MassObserver implements Control {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";
    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int protocolID;

    private double lastMass;

    public MassObserver(String prefix) {
        protocolID = Configuration.getPid(prefix + "." + PAR_PROT);

        lastMass = 0;
        for (int i = 0; i < Network.size(); ++i) {
            PullSum pullSum = (PullSum) Network.get(i).getProtocol(protocolID);
            lastMass += pullSum.getS();
        }
    }

    @Override
    public boolean execute() {
        double sum = 0;
        for (int i = 0; i < Network.size(); ++i) {
            PullSum pullSum = (PullSum) Network.get(i).getProtocol(protocolID);
            sum += pullSum.getS();
        }
        System.out.println("Mass Difference " + (lastMass - sum));
        lastMass = sum;
        return false;
    }
}
