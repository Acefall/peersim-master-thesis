package example.pushMaximize;

import example.AggregationProtocol;
import messagePassing.Message;
import messagePassing.MessagePassing;
import example.RandomLinkable;
import approximation.Approximation;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.Node;

import java.util.Iterator;

public class PushMaximize extends AggregationProtocol implements CDProtocol, Approximation {
    private final String name;
    public PushMaximize(String prefix) {
        this.name = prefix;
    }

    private double currentEstimate = 0.0;

    @Override
    public void nextCycle(Node node, int protocolID) {
        double tempEstimate = currentEstimate;

        boolean updatedFromMessage = processInboundMessages();
        boolean updatedFromInput = getInput() > currentEstimate;
        if(updatedFromInput){

            var x = getInput();


            currentEstimate = getInput();
        }


        if(updatedFromInput || updatedFromMessage){
            int linkableID = FastConfig.getLinkable(protocolID);
            RandomLinkable linkable = (RandomLinkable) node.getProtocol(linkableID);

            Node peer1 = linkable.getRandomNeighbor();
            Node peer2 = linkable.getRandomNeighbor();
            messagePassing.putOutboundMessage(new PushMaximizeMessage(node, peer1, protocolID, currentEstimate));
            messagePassing.putOutboundMessage(new PushMaximizeMessage(node, peer2, protocolID, currentEstimate));
        }
    }

    private boolean processInboundMessages() {
        Iterator<Message> messages = messagePassing.getInBoundMessages();

        boolean updated = false;
        while (messages.hasNext()) {
            Message message = messages.next();
            double maxFromMessage  =((PushMaximizeMessage) message).getMax();
            if(maxFromMessage > currentEstimate){
                currentEstimate = maxFromMessage;
                updated = true;
            }
            messages.remove();
        }
        return updated;
    }


    public Object clone() {
        PushMaximize maxPush = new PushMaximize(name);
        maxPush.messagePassing = new MessagePassing();
        return maxPush;
    }

    private double lastEstimate;
    @Override
    public double getApproximation() {
        return currentEstimate;
    }
}
