package protocols.randomCallPull;

import protocols.approximation.Approximation;
import protocols.approximation.SNApproximation;
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
 * Protocol as described by Schindelhauer et. al.
 */
public class RandomCallPull extends SNApproximation implements CDProtocol, Approximation, PullProtocol {
    protected final String name;

    public RandomCallPull(String prefix) {
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

    /**
     * Every round send a pull request to a random neighbour.
     * For every pull request reply with the current s and n.
     *
     * @param node       The node this instance of the protocol is running on
     * @param protocolID The id of this protocol
     */
    @Override
    public void nextCycle(Node node, int protocolID) {
        processInboundMessages();

        // Get a random neighbour
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        Node peer = linkable.getCommunicationPartner(node);


        messagePassing.putOutboundMessage(new PullCall(node, peer, protocolID));
    }

    /**
     * For every received response update s and n. For every received request reply with s and n.
     */
    protected void processInboundMessages() {
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();

            RandomCallPullResponse response = (RandomCallPullResponse) message;
            setS(getS() + response.getS());
            setN(getN() + response.getN());

            messages.remove();
        }
    }

    public Object clone() {
        RandomCallPull rcPull = new RandomCallPull(name);
        rcPull.messagePassing = new MessagePassing();
        return rcPull;
    }

    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        for (Message pullCall : pullCalls){
            messagePassing.putOutboundMessage(new RandomCallPullResponse(
                    node,
                    pullCall.getSender(),
                    protocolID,
                    getS(),
                    getN()));
        }
    }
}
