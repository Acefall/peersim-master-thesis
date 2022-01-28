package protocols.adaptiveSampling;

import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullProtocol;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Node;
import protocols.approximation.Approximation;
import timeseries.EpochProtocol;
/**
 * Protocol that collects a fixed number of samples in every round.
 * */
public class FixedSamplingRate extends Sampling implements EpochProtocol, CDProtocol, Approximation, PullProtocol {
    private static String PAR_SAMPLING_RATE = "samplingRate";
    private static String PAR_SAMPLE_SIZE = "sampleSize";

    private final int samplingRate;
    private final int sampleSize;


    public FixedSamplingRate(String name) {
        super(name, Configuration.getInt(name + "." + PAR_SAMPLE_SIZE));
        samplingRate = Configuration.getInt(name + "." + PAR_SAMPLING_RATE);
        sampleSize = Configuration.getInt(name + "." + PAR_SAMPLE_SIZE);
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        requestSamples(node, protocolID, samplingRate);
    }

    @Override
    public Object clone() {
        FixedSamplingRate protocol = new FixedSamplingRate(name);
        protocol.messagePassing = new MessagePassing();
        return protocol;
    }

    @Override
    public double getApproximation() {
        return super.getApproximation(sampleSize);
    }
}
