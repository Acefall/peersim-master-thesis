package protocols.baseline;

import outputs.BufferedLogger;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import protocols.AggregationProtocol;
import protocols.approximation.Approximation;
import timeseries.EpochProtocol;


public class IgnoreChanges extends AggregationProtocol implements EpochProtocol, CDProtocol, Approximation {
    private double firstGroundTruth;

    public IgnoreChanges(String name){}

    public void setFirstGroundTruth(double firstGroundTruth) {
        this.firstGroundTruth = firstGroundTruth;
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
    }

    @Override
    public Object clone() {
        return new IgnoreChanges("");
    }

    @Override
    public double getApproximation() {
        return firstGroundTruth;
    }

    @Override
    public void processInboundMessages(Node node, int protocolID) {
    }
}
