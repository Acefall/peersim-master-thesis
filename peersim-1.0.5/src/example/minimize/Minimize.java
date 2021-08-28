package example.minimize;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.vector.SingleValueHolder;

public class Minimize extends SingleValueHolder implements CDProtocol {

    protected static final String PAR_PULL = "pull";

    /** Whether pull or push is used. Obtained from config property {@link #PAR_PULL}. */
    protected final boolean pull;

    // ------------------------------------------------------------------------
    // Initialization
    // ------------------------------------------------------------------------
    public Minimize(String prefix){
        super(prefix);
        pull = Configuration.getBoolean(prefix+"."+PAR_PULL, true);
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        int linkableID = FastConfig.getLinkable(protocolID);
        Linkable linkable = (Linkable) node.getProtocol(linkableID);


        Node peer = linkable.getNeighbor(CommonState.r.nextInt(linkable
                .degree()));

        if (!peer.isUp()){
            return;
        }

        Minimize peerProtocol = (Minimize) peer.getProtocol(protocolID);

        double minValue = Math.min(value, peerProtocol.value);

        if(pull) {
            value = minValue;
        }else{
            peerProtocol.value = minValue;
        }
    }
}
