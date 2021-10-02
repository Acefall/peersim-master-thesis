package example.pullSum;

import approximation.Approximation;
import example.pullSum.PullSum;
import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.IllegalParameterException;
import peersim.core.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PullSumMassConservation extends PullSum implements CDProtocol, Approximation, PullProtocol {
    private final double keepRatio;
    private static final String PAR_KEEP_RATIO = "keepRatio";

    private final String keepFunction;
    private static final String PAR_KEEP_FUNCTION = "keepFunction";

    public PullSumMassConservation(String name){
        super(name);
        keepRatio = Configuration.getDouble(name + "." + PAR_KEEP_RATIO, 0.5);
        keepFunction = Configuration.getString(name + "." + PAR_KEEP_FUNCTION, "Identity");
    }

    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        HashSet<PullSumResponse> replies = new HashSet<>();
        if (pullCalls.size() > 1) { // s and w have to be split among multiple calls
            double keepFactor = keepFactor(keepRatio, keepFunction, pullCalls.size());
            boolean servedMyself = false; // indicates whether a pull call from myself is already answered
            for (Message pullCall : pullCalls) {
                if (!servedMyself && pullCall.getSender().getID() == node.getID()) {
                    messagePassing.putOutboundMessage(new PullSumResponse(
                            node,
                            pullCall.getSender(),
                            protocolID,
                            keepFactor * getS(),
                            keepFactor * getW()));
                    servedMyself = true;
                } else {
                    messagePassing.putOutboundMessage(new PullSumResponse(
                            node,
                            pullCall.getSender(),
                            protocolID,
                            ((1 - keepFactor)/(pullCalls.size() - 1)) * getS(),
                            ((1 - keepFactor)/(pullCalls.size() - 1)) * getW()));
                }
            }
        }else if(pullCalls.size() == 1){ // only received one pull call. The one from my self.
            messagePassing.putOutboundMessage(new PullSumResponse(
                    node,
                    pullCalls.get(0).getSender(),
                    protocolID,
                    getS(),
                    getW()));
        }
    }

    public Object clone() {
        PullSumMassConservation pullSumMC = new PullSumMassConservation(name);
        pullSumMC.messagePassing = new MessagePassing();
        return pullSumMC;
    }

    private double keepFactor(double keepRatio, String keepFunction, int numberOfCalls){
        if(Objects.equals(keepFunction, "Identity")){
            return keepRatio;
        } else if(Objects.equals(keepFunction, "2PowerR")){
            return 1/Math.pow(2, keepRatio);
        } else if(Objects.equals(keepFunction, "MPowerR")){
            return 1/Math.pow(numberOfCalls, keepRatio);
        } else{
            throw new IllegalParameterException(name, "Unknown keep function '" + keepFunction + "'.");
        }
    }
}
