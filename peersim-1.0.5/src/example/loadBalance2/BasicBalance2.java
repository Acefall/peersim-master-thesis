package example.loadBalance2;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.vector.SingleValueHolder;

public class BasicBalance2 extends SingleValueHolder implements CDProtocol {
    // ------------------------------------------------------------------------
    // Parameters
    // ------------------------------------------------------------------------
    protected static final String PAR_QUOTA = "quota";

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    /** Quota amount. Obtained from config property {@link #PAR_QUOTA}. */
    private final double quota_from_config;

    protected double quota; // current cycle quota

    // ------------------------------------------------------------------------
    // Initialization
    // ------------------------------------------------------------------------
    public BasicBalance2(String prefix){
        super(prefix);
        // Get quota from the config file . Default 1.
        quota_from_config = Configuration.getInt(prefix + "." + PAR_QUOTA, 1);
        quota = quota_from_config;
    }

    // Resets the quota
    public void resetQuota() {
        this.quota = this.quota_from_config;
    }


    @Override
    public void nextCycle(Node node, int protocolID) {
        int linkableID = FastConfig.getLinkable(protocolID);
        Linkable linkable = (Linkable) node.getProtocol(linkableID);
        if(this.quota == 0){
            return; // Quota exceeded
        }

        // This takes the most distance neighbour based on local load that still has quota left
        BasicBalance2 neighbour = null;
        double maxDiff = 0;
        for (int i = 0; i< linkable.degree(); i++) {
            Node peer = linkable.getNeighbor(i);
            // The selected peer could be inactive
            if (!peer.isUp()){
                continue;
            }
            BasicBalance2 candidate = (BasicBalance2) peer.getProtocol(protocolID);
            if(candidate.quota == 0){
                continue;
            }
            double difference = Math.abs(value - candidate.value);
            if (difference > maxDiff){
                neighbour = candidate;
                maxDiff = difference;
            }
        }
        if (neighbour == null) {
            return;
        }
        doTransfer(neighbour);

    }

    protected void doTransfer(BasicBalance2 neighbour) {
        double a1 = this.value;
        double a2 = neighbour.value;
        double maxTransfer = Math.abs((a1-a2)/2);
        double transfer = Math.min(maxTransfer, quota);

        if(a1 <= a2){ // transfer load from neighbour to this node
            a1 += transfer;
            a2 -= transfer;
        } else { // transfer load to the neighbour
            a1 -= transfer;
            a2 += transfer;
        }
        this.value = a1;
        this.quota -= transfer;
        neighbour.value = a2;
        neighbour.quota -= transfer;
    }
}
