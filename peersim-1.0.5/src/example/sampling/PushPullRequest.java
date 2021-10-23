package example.sampling;

import messagePassing.Message;
import messagePassing.randomCallModel.PullCall;
import peersim.core.Node;

public class PushPullRequest extends PullCall {
    private double s;
    private double n;

    public double getN() {
        return n;
    }

    public double getS() {
        return s;
    }
    public PushPullRequest(Node sender, Node receiver, int protocolID, double s, double n) {
        super(sender, receiver, protocolID);
        this.s = s;
        this.n = n;
    }
}
