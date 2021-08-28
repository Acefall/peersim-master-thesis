package example.pushSum;

import approximation.Approximation;
import approximation.SWApproximation;
import example.RandomLinkable;
import example.randomCallPush.PushMessage;
import messagePassing.Message;
import messagePassing.MessagePassing;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;

import java.util.Iterator;

/**
 * Push Sum protocol as described by Kempe et. al. extended for time series capabilities.
 * Uses conservation of mass to converge to the true mean.
 */
public class TSPushSum extends SWApproximation implements CDProtocol, Approximation {
    private final String name;
    double oldInput;

    public TSPushSum(String prefix) {
        this.name = prefix;
    }

    @Override
    public void setInput(double input) {
        if(CommonState.getIntTime() == 0) {
            setS(input);
            setW(1);
        }
        super.setInput(input);
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        if(CommonState.getIntTime() == 0){
            oldInput = getInput();
            setS(oldInput);
        }

        if (CommonState.getIntTime() != 0) {
            processInboundMessages(node, protocolID);
        }

        // Get a random neighbour
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomLinkable linkable = (RandomLinkable) node.getProtocol(linkableID);
        Node peer = linkable.getRandomNeighbor();

        double d = getInput() - oldInput;
        setS(getS() + d);
        oldInput = getInput();

        messagePassing.putOutboundMessage(new PushMessage(node, peer, protocolID, getS() / 2, getW() / 2));
        messagePassing.putOutboundMessage(new PushMessage(node, node, protocolID, getS() / 2, getW() / 2));
    }

    private void processInboundMessages(Node node, int protocolID) {
        setS(0);
        setW(0);
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            PushMessage pushMessage = (PushMessage) message;
            setS(getS() + pushMessage.getS());
            setW(getW() + pushMessage.getN());
            messages.remove();
        }
    }


    public Object clone() {
        TSPushSum tsPushSum = new TSPushSum(name);
        tsPushSum.messagePassing = new MessagePassing();
        tsPushSum.setW(1);
        return tsPushSum;
    }
}
