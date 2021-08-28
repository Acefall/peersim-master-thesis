package example.tsHistMean;

import messagePassing.Message;
import peersim.core.Node;

public class TsHistMeanPullResponse extends Message {
    private double[][] histJoin;
    private double[][] histLeave;

    public TsHistMeanPullResponse(Node sender, Node receiver, int protocolID, double[][] histJoin, double[][]histLeave) {
        super(sender, receiver, protocolID);
        this.histJoin = histJoin;
        this.histLeave = histLeave;
    }

    public double[][] getHistJoin() {
        return histJoin;
    }

    public double[][] getHistLeave() {
        return histLeave;
    }
}
