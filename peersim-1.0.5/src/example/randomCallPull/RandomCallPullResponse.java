package example.randomCallPull;

import messagePassing.Message;
import peersim.core.Node;

/**
 * Response sent by RandomCallPull in response to a pull request.
 */
public class RandomCallPullResponse extends Message {
    private final double s;
    private final double n;

    public RandomCallPullResponse(Node sender, Node receiver, int protocolID, double s, double n) {
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
