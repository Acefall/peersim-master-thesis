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

public class Push extends SingleValueHolder implements CDProtocol, MPProtocol {
    private MessagePassing messagePassing = new MessagePassing();

    public Push(String prefix){
        super(prefix);
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        processInboundMessages(node, protocolID);

        int linkableID = FastConfig.getLinkable(protocolID);
        Linkable linkable = (Linkable) node.getProtocol(linkableID);

        Node peer = linkable.getNeighbor(CommonState.r.nextInt(linkable
                .degree()));

        if (!peer.isUp()){
            return;
        }
        messagePassing.putOutboundMessage(new PushMessage(node, peer, protocolID, value));
    }



    private void processInboundMessages(Node node, int protocolID){
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while(messages.hasNext()){
            Message message = messages.next();
            if(message instanceof PushMessage){
                value = Math.min(value, ((PushMessage) message).getValue());
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
        Push push= (Push) super.clone();
        push.messagePassing = new MessagePassing();
        return push;
    }
}