package example.pullSum;

import approximation.Approximation;
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

public class PullSumMassConservation2 extends PullSum implements CDProtocol, Approximation, PullProtocol {
    private final double keepRatio;
    private static final String PAR_KEEP_RATIO = "keepRatio";

    private final int keepFunctionId;
    private String keepFunction = "";
    private static final String PAR_KEEP_FUNCTION = "keepFunction";

    public PullSumMassConservation2(String name){
        super(name);
        keepRatio = Configuration.getDouble(name + "." + PAR_KEEP_RATIO, 0.5);
        keepFunctionId = Configuration.getInt(name + "." + PAR_KEEP_FUNCTION);
        if (keepFunctionId == 1) {
            keepFunction = "numCalls";
        } else if (keepFunctionId == 2) {
            keepFunction = "identity";
        } else if (keepFunctionId == 3) {
            keepFunction = "2PowerR";
        }
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
        PullSumMassConservation2 pullSumMC = new PullSumMassConservation2(name);
        pullSumMC.messagePassing = new MessagePassing();
        return pullSumMC;
    }

    private double keepFactor(double keepRatio, String keepFunction, int numberOfCalls){
        if (Objects.equals(keepFunction, "numCalls")) {
            return 1.0/numberOfCalls;
        } else if (Objects.equals(keepFunction, "identity")) {
            return keepRatio;
        } else if (Objects.equals(keepFunction, "2PowerR")) {
            return 1.0 / Math.pow(2, keepRatio);
        }  else {
            throw new IllegalParameterException(name, "Unknown keep function '" + keepFunction + "'.");
        }
    }
}
