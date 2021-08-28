package example.antiEntropyAggregation;

import messagePassing.Message;
import peersim.core.Node;

public class AntiEntropyAggregationMessage extends Message {
    public AntiEntropyIncrementer getIncrementer() {
        return incrementer;
    }

    public void setIncrementer(AntiEntropyIncrementer incrementer) {
        this.incrementer = incrementer;
    }

    private AntiEntropyIncrementer incrementer;
    public AntiEntropyAggregationMessage(Node sender, Node receiver, int protocolID, AntiEntropyIncrementer incrementer) {
        super(sender, receiver, protocolID);
        setIncrementer(incrementer);
    }
}
