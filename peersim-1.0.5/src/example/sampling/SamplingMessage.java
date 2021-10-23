package example.sampling;

import messagePassing.Message;
import peersim.core.Node;


public class SamplingMessage extends Message {
    private final double s;
    private final double n;

    public SamplingMessage(Node sender, Node receiver, int protocolID, double s, double n) {
        super(sender, receiver, protocolID);
        this.n = n;
        this.s = s;
    }

    public double getN() {
        return n;
    }

    public double getS() {
        return s;
    }
}
