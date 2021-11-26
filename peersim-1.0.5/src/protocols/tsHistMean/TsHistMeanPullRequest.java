package protocols.tsHistMean;

import messagePassing.Message;
import peersim.core.Node;

public class TsHistMeanPullRequest extends Message {
    public TsHistMeanPullRequest(Node sender, Node receiver, int protocolID) {
        super(sender, receiver, protocolID);
    }
}
