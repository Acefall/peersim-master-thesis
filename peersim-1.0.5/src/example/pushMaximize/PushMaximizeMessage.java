package example.pushMaximize;

import messagePassing.Message;
import peersim.core.Node;

public class PushMaximizeMessage extends Message {
    private double max;

    public double getMax() {
        return max;
    }


    public PushMaximizeMessage(Node sender, Node receiver, int protocolID, double max) {
        super(sender, receiver, protocolID);
        this.max = max;
    }
}
