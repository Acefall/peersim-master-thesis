// Author: Alexander Weinmann uni@aweinmann.de
package protocols.pullSum;

import protocols.approximation.Approximation;
import protocols.approximation.SWApproximation;
import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;

import java.util.Iterator;
import java.util.List;

/**
 * Adapted Pull Sum protocol to study the smallest possible weight.
 * Nodes ignore all incoming weight.
 */
public class PullSumNoIncoming extends SWApproximation implements CDProtocol, Approximation, PullProtocol, HasWeight {
    protected final String name;


    public PullSumNoIncoming(String prefix) {
        this.name = prefix;
    }

    @Override
    public void setInput(double input) {
        if (CommonState.getIntTime() == 0) {
            setS(input);
            setW(1);
        }
        super.setInput(input);
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        // Get a random neighbour
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        Node peer = linkable.getCommunicationPartner(node);


        messagePassing.putOutboundMessage(new PullCall(node, peer, protocolID));
        messagePassing.putOutboundMessage(new PullCall(node, node, protocolID));
        if (CommonState.getIntTime() != 0) {
            processResponses();
        }

    }

    private void processResponses() {
        setS(0);
        setW(0);
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            if (message instanceof PullSumResponse) {
                PullSumResponse response = (PullSumResponse) message;
                if(response.getSender().getID() == response.getReceiver().getID()){
                    setS(getS() + response.getS());
                    setW(getW() + response.getW());
                }
                messages.remove();
            }
        }
    }

    public Object clone() {
        PullSumNoIncoming pullSum = new PullSumNoIncoming(name);
        pullSum.messagePassing = new MessagePassing();
        return pullSum;
    }

    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        for (Message pullCall : pullCalls) {
            messagePassing.putOutboundMessage(new PullSumResponse(
                    node,
                    pullCall.getSender(),
                    protocolID,
                    getS() / pullCalls.size(),
                    getW() / pullCalls.size()));
        }
    }

    @Override
    public double getWeight() {
        return getW();
    }
}
