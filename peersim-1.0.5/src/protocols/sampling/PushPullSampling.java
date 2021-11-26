package protocols.sampling;

import protocols.approximation.Approximation;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;

import java.util.List;


public class PushPullSampling extends SamplingProtocol implements CDProtocol, Approximation, PullProtocol {
    private final String name;

    public PushPullSampling(String prefix) {
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

            messagePassing.putOutboundMessage(new PushPullRequest(node, peer, protocolID, getS(), getN()));
        }
    }

    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        for (PullCall pullCall : pullCalls) {
            messagePassing.putOutboundMessage(new SamplingMessage(
                    node,
                    pullCall.getSender(),
                    protocolID,
                    getS(),
                    getN()));
        }
        for (PullCall pullCall : pullCalls) {
            PushPullRequest request = (PushPullRequest) pullCall;
            setS(getS() + request.getS());
            setN(getN() + request.getN());
        }
    }


    public Object clone() {
        PushPullSampling pushPullSampling = new PushPullSampling(name);
        pushPullSampling.messagePassing = new MessagePassing();
        return pushPullSampling;
    }
}
