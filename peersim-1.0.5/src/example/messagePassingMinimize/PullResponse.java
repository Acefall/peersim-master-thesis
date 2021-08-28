package example.messagePassingMinimize;

import messagePassing.Message;
import peersim.core.Node;
import peersim.vector.SingleValue;

public class PullResponse extends Message implements SingleValue {
    private double value;

    public PullResponse(Node sender, Node receiver, int protocolID, double value) {
        super(sender, receiver, protocolID);
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public void setValue(double value) {
        this.value = value;
    }
}
