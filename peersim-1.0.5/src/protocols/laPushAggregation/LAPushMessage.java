package protocols.laPushAggregation;

import messagePassing.Message;
import peersim.core.Node;

public class LAPushMessage extends Message {
    double estiamte;

    public double getEstiamte() {
        return estiamte;
    }

    public LAPushMessage(Node sender, Node receiver, int protocolID, double estiamte) {
        super(sender, receiver, protocolID);
        this.estiamte = estiamte;
    }
}
