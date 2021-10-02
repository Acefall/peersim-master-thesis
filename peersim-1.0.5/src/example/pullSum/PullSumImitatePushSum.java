package example.pullSum;

import approximation.Approximation;
import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class PullSumImitatePushSum extends PullSum implements CDProtocol, Approximation, PullProtocol {

    public PullSumImitatePushSum(String name){
        super(name);
    }

    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        HashSet<PullSumResponse> replies = new HashSet<>();
        if (pullCalls.size() > 1) { // s and w have to be split among multiple calls
            boolean servedMyself = false; // indicates whether a pull call from myself is already answered
            Collections.shuffle(pullCalls, CommonState.r);
            for (int i = 0; i < pullCalls.size(); i++) {
                PullCall pullCall = pullCalls.get(i);
                if (!servedMyself && pullCall.getSender().getID() == node.getID()) {
                    messagePassing.putOutboundMessage(new PullSumResponse(
                            node,
                            pullCall.getSender(),
                            protocolID,
                            1/(Math.pow(2, pullCalls.size() - 1)) * getS(),
                            1/(Math.pow(2, pullCalls.size() - 1)) * getW()));
                    servedMyself = true;
                } else {
                    double factor;
                    if(!servedMyself){
                        factor = 1/Math.pow(2, i+1);
                    }else{
                        factor = 1/Math.pow(2, i);
                    }
                    messagePassing.putOutboundMessage(new PullSumResponse(
                            node,
                            pullCall.getSender(),
                            protocolID,
                            factor * getS(),
                            factor  * getW()));
                }
            }
        }else if(pullCalls.size() == 1){ // only received one pull call. The one from myself.
            messagePassing.putOutboundMessage(new PullSumResponse(
                    node,
                    pullCalls.get(0).getSender(),
                    protocolID,
                    getS(),
                    getW()));
        }
    }

    public Object clone() {
        PullSumImitatePushSum protocol = new PullSumImitatePushSum(name);
        protocol.messagePassing = new MessagePassing();
        return protocol;
    }
}
