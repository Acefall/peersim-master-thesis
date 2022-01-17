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

    private static String PAR_SAMPLE_SIZE = "sampleSize";
    private final int sampleSize;

    private int samplesCollected = 0;

    public ConstantSamplingRate(String name) {
        super(name, 100);
        samplingRate = Configuration.getDouble(name + "." + PAR_SAMPLING_RATE);
        sampleSize = Configuration.getInt(name + "." + PAR_SAMPLE_SIZE);
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        int targetSamples = (int) Math.ceil((CommonState.getIntTime()+1)*samplingRate);
        requestSamples(node, protocolID, targetSamples - samplesCollected);
        samplesCollected = targetSamples;
    }

    @Override
    public Object clone() {
        ConstantSamplingRate protocol = new ConstantSamplingRate(name);
        protocol.messagePassing = new MessagePassing();
        return protocol;
    }

    @Override
    public double getApproximation() {
        return super.getApproximation(sampleSize);
    }
}
