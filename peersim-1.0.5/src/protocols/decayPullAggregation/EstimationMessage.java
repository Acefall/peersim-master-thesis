package protocols.decayPullAggregation;

import messagePassing.Message;
import peersim.core.Node;

public class EstimationMessage extends Message {
    private double estimation;

    public double getEstimation() {
        return estimation;
    }

    public void setEstimation(double estimation) {
        this.estimation = estimation;
    }

    public EstimationMessage(Node sender, Node receiver, int protocolID, double estimation) {
        super(sender, receiver, protocolID);
        this.estimation = estimation;
    }
}
