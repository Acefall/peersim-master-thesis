package protocols.sampling;

import protocols.approximation.Approximation;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;



public class PushSampling extends SamplingProtocol implements CDProtocol, Approximation {
    private final String name;

    public PushSampling(String prefix) {
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

            messagePassing.putOutboundMessage(new SamplingMessage(node, peer, protocolID, getS(), getN()));
        }
    }

    public Object clone() {
        PushSampling pushSampling = new PushSampling(name);
        pushSampling.messagePassing = new MessagePassing();
        return pushSampling;
    }
}
