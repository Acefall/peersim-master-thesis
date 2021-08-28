package example.randomCallPush;

import messagePassing.Message;
import messagePassing.MessagePassing;
import example.RandomLinkable;
import approximation.Approximation;
import approximation.SNApproximation;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;

import java.util.Iterator;

/**
 * Variation of the random call pull protocol. In this case instead of pulling s and n from a random neighbour,
 * s and n are pushed to a random neighbour.
 */
public class RandomCallPush extends SNApproximation implements CDProtocol, Approximation {
    private final String name;

    public RandomCallPush(String prefix) {
        this.name = prefix;
    }

    @Override
    public void setInput(double input) {
        if(CommonState.getIntTime() == 0) {
            setS(input);
            setN(1);
        }
        super.setInput(input);
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        processInboundMessages(node, protocolID);

        // Get a random neighbour
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        Node peer = linkable.getCommunicationPartner(node);

        messagePassing.putOutboundMessage(new PushMessage(node, peer, protocolID, getS(), getN()));
    }

    private void processInboundMessages(Node node, int protocolID) {
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            PushMessage pushMessage = (PushMessage) message;
            setS(getS() + pushMessage.getS());
            setN(getN() + pushMessage.getN());
            messages.remove();
        }
    }


    public Object clone() {
        RandomCallPush rcPush = new RandomCallPush(name);
        rcPush.messagePassing = new MessagePassing();
        return rcPush;
    }
}
