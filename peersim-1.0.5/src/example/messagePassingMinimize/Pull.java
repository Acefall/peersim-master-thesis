package example.messagePassingMinimize;

import messagePassing.MPProtocol;
import messagePassing.Message;
import messagePassing.MessagePassing;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.vector.SingleValueHolder;

import java.util.Iterator;

/**
 * This protocol aggregates the minimum at all nodes.
 * Each round a pull request is sent to a random neighbour.
 * A pull request is answered with a pull response which contains the current aggregated minimum.
 * */
public class Pull extends SingleValueHolder implements CDProtocol, MPProtocol {
    private MessagePassing messagePassing = new MessagePassing();

    public Pull(String prefix){
        super(prefix);
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        processInboundMessages(node, protocolID);

        // Get a random neighbour
        int linkableID = FastConfig.getLinkable(protocolID);
        Linkable linkable = (Linkable) node.getProtocol(linkableID);
        Node peer = linkable.getNeighbor(CommonState.r.nextInt(linkable
                .degree()));

        if (!peer.isUp()){
            return;
        }

        messagePassing.putOutboundMessage(new PullRequest(node, peer, protocolID));
    }

    private void processInboundMessages(Node node, int protocolID){
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while(messages.hasNext()){
            Message message = messages.next();
            if(message instanceof PullRequest){
                messagePassing.putOutboundMessage(new PullResponse(node, message.getSender(), protocolID, value));
            } else if(message instanceof PullResponse) {
                value = Math.min(value, ((PullResponse) message).getValue());
            }
            messages.remove();
        }
    }


    @Override
    public Iterator<Message> getOutBoundMessages() {
        return messagePassing.getOutBoundMessages();
    }

    @Override
    public Iterator<Message> getInBoundMessages() {
        return messagePassing.getInBoundMessages();
    }

    @Override
    public void putInboundMessage(Message m) {
        messagePassing.putInboundMessage(m);
    }

    public Object clone()
    {
        Pull pull= (Pull) super.clone();
        pull.messagePassing = new MessagePassing();
        return pull;
    }
}
