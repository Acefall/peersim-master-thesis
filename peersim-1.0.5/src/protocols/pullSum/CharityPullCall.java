package protocols.pullSum;

import messagePassing.randomCallModel.PullCall;
import peersim.core.Node;

public class CharityPullCall extends PullCall {
    public double getWeight() {
        return weight;
    }

    private double weight;
    public CharityPullCall(Node sender, Node receiver, int protocolID, double weight) {
        super(sender, receiver, protocolID);
        this.weight = weight;
    }
}
