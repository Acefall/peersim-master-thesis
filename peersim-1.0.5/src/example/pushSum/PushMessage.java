package example.pushSum;

import messagePassing.Message;
import peersim.core.Node;

public class PushMessage extends Message {
    private double s;
    private double w;

    public double getW() {
        return w;
    }

    public double getS() {
        return s;
    }

    public PushMessage(Node sender, Node receiver, int protocolID, double s, double w) {
        super(sender, receiver, protocolID);
        this.w = w;
        this.s = s;
    }
}
