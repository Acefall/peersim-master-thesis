package example.randomCallPush;

import messagePassing.Message;
import peersim.core.Node;

public class PushMessage extends Message {
    private double s;
    private double n;

    public double getN() {
        return n;
    }

    public double getS() {
        return s;
    }

    public PushMessage(Node sender, Node receiver, int protocolID, double s, double n) {
        super(sender, receiver, protocolID);
        this.n = n;
        this.s = s;
    }
}
