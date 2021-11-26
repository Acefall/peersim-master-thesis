package protocols.adaptiveSampling;

import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullProtocol;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import protocols.approximation.Approximation;

public class ConstantSamplingRate extends Sampling implements CDProtocol, Approximation, PullProtocol {
    private static String PAR_SAMPLING_RATE = "samplingRate";
    private final double samplingRate;

    public ConstantSamplingRate(String name) {
        super(name);
        samplingRate = Configuration.getDouble(name + "." + PAR_SAMPLING_RATE);
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        processResponses();

        int targetSamples = (int) Math.ceil((CommonState.getIntTime()+1)*samplingRate);
        requestSamples(node, protocolID, targetSamples - samples.size());
    }

    @Override
    public Object clone() {
        ConstantSamplingRate protocol = new ConstantSamplingRate(name);
        protocol.messagePassing = new MessagePassing();
        return protocol;
    }

    @Override
    public double getApproximation() {
        return super.getApproximation(50);
    }
}
