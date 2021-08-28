package messagePassing.randomCallModel;

import messagePassing.Message;
import peersim.core.Node;

public class PullCall extends Message {
    public PullCall(Node sender, Node receiver, int protocolID) {
        super(sender, receiver, protocolID);
    }
}
