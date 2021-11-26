package protocols.adaptiveSampling;

import messagePassing.Message;
import peersim.core.Node;

public class WakeUpSamplingMessage extends SamplingMessage {
    public WakeUpSamplingMessage(Node sender, Node receiver, int protocolID, Sample sample) {
        super(sender, receiver, protocolID, sample);
    }
}
