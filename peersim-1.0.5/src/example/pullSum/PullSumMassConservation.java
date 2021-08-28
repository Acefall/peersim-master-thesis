package example.pullSum;

import approximation.Approximation;
import example.pullSum.PullSum;
import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PullSumMassConservation extends PullSum implements CDProtocol, Approximation, PullProtocol {
    private final double keepRatio;
    private static final String PAR_KEEP_RATIO = "keepRatio";
    public PullSumMassConservation(String name){
        super(name);
        keepRatio = Configuration.getDouble(name + "." + PAR_KEEP_RATIO, 0.5);
    }

    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        HashSet<PullSumResponse> replies = new HashSet<>();
        if (pullCalls.size() > 1) { // implies at least one pull call from a different node
            for (Message pullCall : pullCalls) {
                if (pullCall.getSender().getID() == node.getID()) {
                    messagePassing.putOutboundMessage(new PullSumResponse(
                            node,
                            pullCall.getSender(),
                            protocolID,
                            keepRatio * getS(),
                            keepRatio * getW()));
                    replies.add(new PullSumResponse(
                            node,
                            pullCall.getSender(),
                            protocolID,
                            keepRatio * getS(),
                            keepRatio * getW()));
                } else {
                    messagePassing.putOutboundMessage(new PullSumResponse(
                            node,
                            pullCall.getSender(),
                            protocolID,
                            ((1 - keepRatio)/(pullCalls.size() - 1)) * getS(),
                            ((1 - keepRatio)/(pullCalls.size() - 1)) * getW()));
                    replies.add(new PullSumResponse(
                            node,
                            pullCall.getSender(),
                            protocolID,
                            ((1 - keepRatio)/(pullCalls.size() - 1)) * getS(),
                            ((1 - keepRatio)/(pullCalls.size() - 1)) * getW()));
                }

            }
        }else if(pullCalls.size() == 1){ // only received one pull call. The one from my self.
            messagePassing.putOutboundMessage(new PullSumResponse(
                    node,
                    pullCalls.get(0).getSender(),
                    protocolID,
                    getS(),
                    getW()));
            replies.add(new PullSumResponse(
                    node,
                    pullCalls.get(0).getSender(),
                    protocolID,
                    getS(),
                    getW()));
        }

        double sum = 0;
        for (PullSumResponse reply : replies){
            sum += reply.getS();
        }
        if(sum == getS()){
            //System.out.println("Mass Conserved");
        }else{
            System.out.println(Math.abs(sum - getS()));
        }

    }

    public Object clone() {
        PullSumMassConservation pullSumMC = new PullSumMassConservation(name);
        pullSumMC.messagePassing = new MessagePassing();
        return pullSumMC;
    }
}
