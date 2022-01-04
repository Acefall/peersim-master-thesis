package protocols.laPushAggregation;

import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;
import protocols.AggregationProtocol;
import protocols.approximation.Approximation;
import protocols.approximation.SNApproximation;
import protocols.randomCallPush.PushMessage;

import java.util.Iterator;

/**
 * Local averaging push protocol.
 */
public class LAPushAggregation extends AggregationProtocol implements CDProtocol, Approximation {
    private final String name;
    private double estimate;


    public LAPushAggregation(String prefix) {
        this.name = prefix;
    }

    @Override
    public void setInput(double input) {
        if(CommonState.getIntTime() == 0) {
            estimate = input;
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

        messagePassing.putOutboundMessage(new LAPushMessage(node, peer, protocolID, estimate));
    }

    private void processInboundMessages(Node node, int protocolID) {
        int messageCount = 1;
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            estimate += ((LAPushMessage) message).getEstiamte();
            messageCount += 1;
            messages.remove();
        }
        estimate = estimate/messageCount;
    }


    public Object clone() {
        LAPushAggregation laPushAggregation = new LAPushAggregation(name);
        laPushAggregation.messagePassing = new MessagePassing();
        return laPushAggregation;
    }

    @Override
    public double getApproximation() {
        return estimate;
    }
}
