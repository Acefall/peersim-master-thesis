package protocols.adaptiveSampling;

import messagePassing.Message;
import peersim.core.Node;


public class SamplingMessage extends Message {
    private final Sample sample;

    public SamplingMessage(Node sender, Node receiver, int protocolID, Sample sample) {
        super(sender, receiver, protocolID);
        this.sample = sample;
    }

    public Sample getSample() {
        return sample;
    }
}
