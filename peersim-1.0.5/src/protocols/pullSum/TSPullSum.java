// Author: Alexander Weinmann uni@aweinmann.de
package protocols.pullSum;

import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import messagePassing.randomCallModel.RandomCallModel;
import protocols.approximation.Approximation;
import protocols.approximation.SWApproximation;
import network.RandomLinkable;
import messagePassing.Message;
import messagePassing.MessagePassing;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;
import timeseries.EpochProtocol;

import java.util.Iterator;
import java.util.List;

/**
 * Pull Sum protocol that is mass conserving even under changing inputs.
 */
public class TSPullSum extends SWApproximation implements EpochProtocol, CDProtocol, Approximation, PullProtocol {
    private final String name;
    private double oldInput;

    public TSPullSum(String prefix) {
        this.name = prefix;
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        if(CommonState.getIntTime() == 0){
            oldInput = getInput();
            setS(oldInput);
        }

        double d = getInput() - oldInput;
        setS(getS() + d);
        oldInput = getInput();

        // Get a random neighbour
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        Node peer = linkable.getCommunicationPartner(node);

        messagePassing.putOutboundMessage(new PullCall(node, peer, protocolID));
    }
    @Override
    public void processInboundMessages(Node node, int protocolID) {
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            if(message instanceof PullSumResponse){
                PullSumResponse response = (PullSumResponse) message;
                setS(getS() + response.getS());
                setW(getW() + response.getW());
                messages.remove();
            }
        }
    }


    public Object clone() {
        TSPullSum tsPullSum = new TSPullSum(name);
        tsPullSum.messagePassing = new MessagePassing();
        tsPullSum.setW(1);
        return tsPullSum;
    }


    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        for (Message pullCall : pullCalls) {
            messagePassing.putOutboundMessage(new PullSumResponse(
                    node,
                    pullCall.getSender(),
                    protocolID,
                    getS() / (pullCalls.size()+1),
                    getW() / (pullCalls.size()+1)));
        }
        setS(getS() / (pullCalls.size()+1));
        setW(getW() / (pullCalls.size()+1));
    }
}
