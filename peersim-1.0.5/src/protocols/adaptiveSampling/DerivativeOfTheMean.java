package protocols.adaptiveSampling;

import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullProtocol;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Node;
import protocols.approximation.Approximation;


public class DerivativeOfTheMean extends Sampling implements CDProtocol, Approximation, PullProtocol {
    private static String PAR_SENSITIVITY = "sensitivity";
    private static String PAR_MAX_SAMPLING_RATE = "maxSamplingRate";

    private double lastApproximation;
    private final int maxSamplingRate;
    private final double sensitivity;

    public DerivativeOfTheMean(String name) {
        super(name, Configuration.getInt(name + "." + PAR_MAX_SAMPLING_RATE));
        sensitivity = Configuration.getDouble(name + "." + PAR_SENSITIVITY);
        maxSamplingRate = Configuration.getInt(name + "." + PAR_MAX_SAMPLING_RATE);
    }


    @Override
    public void nextCycle(Node node, int protocolID) {
        double newApproximation = getApproximation();
        double absoluteDerivative = Math.abs(newApproximation - lastApproximation);
        lastApproximation = newApproximation;

        double samplesToRequest = Math.max(
                1,
                Math.min(
                        maxSamplingRate,
                        Math.ceil(absoluteDerivative * sensitivity)
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
        return super.getApproximation(maxSamplingRate);
    }
}
