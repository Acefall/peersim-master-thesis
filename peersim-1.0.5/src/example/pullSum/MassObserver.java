package example.pullSum;

import example.AggregationProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

public class MassObserver implements Control {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";
    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int protocolID;

    public MassObserver(String prefix) {
        protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
    }

    @Override
    public boolean execute() {
        double sum = 0;
        for (int i = 0; i < Network.size(); ++i) {
            PullSum pullSum = (PullSum) Network.get(i).getProtocol(protocolID);
            sum += pullSum.getS();
        }
        System.out.println(sum);
        return false;
    }
}
