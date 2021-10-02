// Author: Alexander Weinmann uni@aweinmann.de
package example.pullSum;

import messagePassing.Message;
import peersim.core.Node;

public class PullSumVectorResponse extends Message {
    private final Double[] contributions;

    public PullSumVectorResponse(Node sender, Node receiver, int protocolID, Double[] contributions) {
        super(sender, receiver, protocolID);
        this.contributions = contributions;
    }

    public Double[] getContributions() {
        return contributions;
    }
}
