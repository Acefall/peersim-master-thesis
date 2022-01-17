// Author: Alexander Weinmann uni@aweinmann.de
package messagePassing;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import protocols.pushSum.TSPushSum;
import timeseries.EpochProtocol;
import timeseries.TSProtocol;

import java.util.Iterator;

/**
 * This class provides message delivery in the cycle driven simulation.
 */
public class InboundMessageManager implements Control {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";

    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    protected final int pid;


    public InboundMessageManager(String name) {
        pid = Configuration.getPid(name + "." + PAR_PROT);
    }

    public boolean execute() {
        final int len = Network.size();
        for (int i = 0; i < len; i++) {
            EpochProtocol epochProtocol = (EpochProtocol) Network.get(i).getProtocol(pid);
            epochProtocol.processInboundMessages(Network.get(i), pid);
        }
        return false;
    }

}
