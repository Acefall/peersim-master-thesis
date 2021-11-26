// Author: Alexander Weinmann uni@aweinmann.de
package messagePassing;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

import java.util.Iterator;

/**
 * This class provides message delivery in the cycle driven simulation.
 */
public class MessageManager implements Control {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";

    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    protected final int pid;


    public MessageManager(String name) {
        pid = Configuration.getPid(name + "." + PAR_PROT);
    }

    public boolean execute() {
        final int len = Network.size();
        for (int i = 0; i < len; i++) {
            MPProtocol messagePassing = (MPProtocol) Network.get(i).getProtocol(pid);
            Iterator<Message> outBoundMessages = messagePassing.getOutBoundMessages();
            while (outBoundMessages.hasNext()) {
                Message m = outBoundMessages.next();
                if (m.getReceiver().isUp()) {
                    m.getReceiverProtocol().putInboundMessage(m);
                }
                outBoundMessages.remove();

            }
        }
        return false;
    }

}
