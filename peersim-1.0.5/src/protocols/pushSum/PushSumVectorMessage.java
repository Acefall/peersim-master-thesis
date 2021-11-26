// Author: Alexander Weinmann uni@aweinmann.de
package protocols.pushSum;

import messagePassing.Message;
import peersim.core.Node;

public class PushSumVectorMessage extends Message {
    private final Double[] contributions;

    public PushSumVectorMessage(Node sender, Node receiver, int protocolID, Double[] contributions) {
        super(sender, receiver, protocolID);
        this.contributions = contributions;
    }

    public Double[] getContributions() {
        return contributions;
    }
}
