package example.sampling;

import approximation.Approximation;
import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;
import java.util.List;


public class PullSampling extends SamplingProtocol implements CDProtocol, Approximation, PullProtocol {
    private final String name;

    public PullSampling(String prefix) {
        super(prefix);
        this.name = prefix;
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        if (CommonState.getIntTime() != 0) {
            processInboundMessages();
        }

        if(getN() < optimalSampleSize){
            // Get a random neighbour
            int linkableID = FastConfig.getLinkable(protocolID);
            RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
            Node peer = linkable.getCommunicationPartner(node);

            messagePassing.putOutboundMessage(new PullCall(node, peer, protocolID));
        }
    }

    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        for (Message pullCall : pullCalls) {
            messagePassing.putOutboundMessage(new SamplingMessage(
                    node,
                    pullCall.getSender(),
                    protocolID,
                    getS(),
                    getN()));
        }
    }


    public Object clone() {
        PullSampling pullSampling = new PullSampling(name);
        pullSampling.messagePassing = new MessagePassing();
        return pullSampling;
    }
}
