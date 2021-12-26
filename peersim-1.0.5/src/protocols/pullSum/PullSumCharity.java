package protocols.pullSum;

import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;
import protocols.approximation.Approximation;
import protocols.approximation.SWApproximation;

import java.util.Iterator;
import java.util.List;

public class PullSumCharity extends SWApproximation implements CDProtocol, Approximation, PullProtocol, HasWeight{
    private String name;

    public PullSumCharity(String prefix) {
        this.name = prefix;
    }

    @Override
    public double getWeight() {
        return getW();
    }

    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        // Calculate normalizing factor
        double z = 0;
        for (PullCall pullCall : pullCalls) {
            CharityPullCall charityPullCall = (CharityPullCall) pullCall;
            z += 1/charityPullCall.getWeight();
        }

        for (Message pullCall : pullCalls) {
            CharityPullCall charityPullCall = (CharityPullCall) pullCall;
            messagePassing.putOutboundMessage(
                    new PullSumResponse(
                        node,
                        charityPullCall.getSender(),
                        protocolID,
                        getS() / (charityPullCall.getWeight() * z),
                        getW() / (charityPullCall.getWeight() * z)
                    )
            );
        }
        setS(0);
        setW(0);
    }

    private void processResponses() {
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            if (message instanceof PullSumResponse) {
                PullSumResponse response = (PullSumResponse) message;
                setS(getS() + response.getS());
                setW(getW() + response.getW());
                messages.remove();
            }
        }

    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        if (CommonState.getIntTime() != 0) {
            processResponses();
        }
        // Get a random neighbour
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        Node peer = linkable.getCommunicationPartner(node);


        messagePassing.putOutboundMessage(new CharityPullCall(node, peer, protocolID, getWeight()));
        messagePassing.putOutboundMessage(new CharityPullCall(node, node, protocolID, getWeight()));
    }

    @Override
    public Object clone() {
        PullSumCharity pullSumCharity = new PullSumCharity(name);
        pullSumCharity.messagePassing = new MessagePassing();
        return pullSumCharity;
    }

    @Override
    public void setInput(double input) {
        if (CommonState.getIntTime() == 0) {
            setS(input);
            setW(1);
        }
        super.setInput(input);
    }
}
