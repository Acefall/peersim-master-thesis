package example.antiEntropyAggregation;

import messagePassing.Message;
import peersim.core.Node;

public class HistAntiEntropyMessage extends Message {
    public Histogram getHistogram() {
        return histogram;
    }

    private Histogram histogram;
    public HistAntiEntropyMessage(Node sender, Node receiver, int protocolID, Histogram histogram) {
        super(sender, receiver, protocolID);
        this.histogram = histogram;
    }
}
