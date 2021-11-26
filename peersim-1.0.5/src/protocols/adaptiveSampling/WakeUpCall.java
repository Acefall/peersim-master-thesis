package protocols.adaptiveSampling;

import messagePassing.Message;
import peersim.core.Node;

public class WakeUpCall extends Message {
    private int wakeUpId;

    public int getWakeUpId() {
        return wakeUpId;
    }

    public void setWakeUpId(int wakeUpId) {
        this.wakeUpId = wakeUpId;
    }

    public WakeUpCall(Node sender, Node receiver, int protocolID, int wakeUpId) {
        super(sender, receiver, protocolID);
        this.wakeUpId = wakeUpId;
    }
}
