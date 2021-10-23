package example.sampling;

import approximation.SNApproximation;
import messagePassing.Message;
import org.apache.commons.math3.special.Erf;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;

import java.util.Iterator;

public abstract class SamplingProtocol extends SNApproximation {
    private final double marginOfError;
    private static final String PAR_MARGIN_OF_ERROR = "marginOfError";

    protected int optimalSampleSize;

    public SamplingProtocol(String name){
        marginOfError = Configuration.getDouble(name  + "." + PAR_MARGIN_OF_ERROR);
        optimalSampleSize = optimalSampleSize(0.95, marginOfError, 0.5, Network.size());
    }

    private static double pToZ(double p) {
        return Math.sqrt(2) * Erf.erfcInv(2*p);
    }

    private static int optimalSampleSize(double p, double marginOfError, double standardDeviation, double populationSize) {
        double a = (1 - p) / 2;
        double ZScore = pToZ(a);
        double n = ((ZScore * ZScore) * (standardDeviation *  standardDeviation)) / (marginOfError  * marginOfError);

        return (int) Math.ceil(n / (1 + (n / populationSize)));
    }

    public void setInput(double input) {
        if(CommonState.getIntTime() == 0) {
            setS(input);
            setN(1);
        }
        super.setInput(input);
    }

    protected void processInboundMessages() {
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            SamplingMessage pushMessage = (SamplingMessage) message;
            setS(getS() + pushMessage.getS());
            setN(getN() + pushMessage.getN());
            messages.remove();
        }
    }
}
