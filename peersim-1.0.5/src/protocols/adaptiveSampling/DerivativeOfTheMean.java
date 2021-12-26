package protocols.adaptiveSampling;

import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullProtocol;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Node;
import protocols.approximation.Approximation;


public class DerivativeOfTheMean extends Sampling implements CDProtocol, Approximation, PullProtocol {
    private static String PAR_MESSAGE_INTENSITY = "messageIntensity";

    private double lastApproximation;
    private final int goodSampleSize = 50;
    private final double messageIntensity;

    public DerivativeOfTheMean(String name) {
        super(name, 50);
        messageIntensity = Configuration.getDouble(name + "." + PAR_MESSAGE_INTENSITY);
    }


    @Override
    public void nextCycle(Node node, int protocolID) {
        processResponses();
        double newApproximation = getApproximation();
        double absoluteDerivative = Math.abs(newApproximation - lastApproximation);
        lastApproximation = newApproximation;

        double samplesToRequest = Math.max(
                1,
                Math.min(
                        goodSampleSize,
                        Math.ceil(absoluteDerivative * goodSampleSize * messageIntensity)
                )
        );

        requestSamples(node, protocolID, (int) samplesToRequest);
    }

    @Override
    public Object clone() {
        DerivativeOfTheMean protocol = new DerivativeOfTheMean(name);
        protocol.messagePassing = new MessagePassing();
        return protocol;
    }

    @Override
    public double getApproximation() {
        return super.getApproximation(goodSampleSize);
    }


}
