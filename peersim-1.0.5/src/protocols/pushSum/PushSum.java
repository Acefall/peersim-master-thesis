package protocols.pushSum;

import protocols.approximation.SWApproximation;
import protocols.pullSum.HasWeight;
import messagePassing.Message;
import messagePassing.MessagePassing;
import protocols.approximation.Approximation;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;

import java.util.Iterator;

/**
 * Push Sum protocol as described by Kempe et. al.
 * Uses conservation of mass to converge to the true mean.
 * Modified to allow for other rations when sending s and n.
 */
public class PushSum extends SWApproximation implements CDProtocol, Approximation, HasWeight {
    private final String name;
    private final double keepRatio;
    private static final String PAR_KEEP_RATIO = "keepRatio";


    public PushSum(String prefix) {
        this.name = prefix;
        keepRatio = Configuration.getDouble(name  + "." + PAR_KEEP_RATIO, 0.5);
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
        if (CommonState.getIntTime() != 0) {
            processInboundMessages(node, protocolID);
        }

        // Get a random neighbour
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        Node peer = linkable.getCommunicationPartner(node);

        messagePassing.putOutboundMessage(new PushMessage(node, peer, protocolID, (1-keepRatio) * getS(), (1-keepRatio) * getW()));
        setS(keepRatio * getS());
        setW(keepRatio * getW());
    }

    private void processInboundMessages(Node node, int protocolID) {
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            PushMessage pushMessage = (PushMessage) message;
            setS(getS() + pushMessage.getS());
            setW(getW() + pushMessage.getW());
            messages.remove();
        }
    }


    public Object clone() {
        PushSum pushSum = new PushSum(name);
        pushSum.messagePassing = new MessagePassing();
        return pushSum;
    }

    @Override
    public double getWeight() {
        return getW();
    }
}
