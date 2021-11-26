package messagePassing.randomCallModel;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

/**
 * Default random phone call model as described by Karp et al.
 * */

public class VanillaRandomCallModel implements RandomCallModel{
    /**
     * Parameter that defines whether self calls are allowed or not.
     */
    private static final String PAR_NO_SELFCALLS = "noSelfCalls";
    /** Whether self calls are allowed or not, obtained from config property {@link #PAR_NO_SELFCALLS}. */
    private final boolean noSelfCalls;

    @Override
    public Node getCommunicationPartner(Node caller) {
        int callerindex = caller.getIndex();
        int randomIndex = -1;
        do {
            randomIndex = CommonState.r.nextInt(Network.size());
        } while (noSelfCalls && callerindex == randomIndex);
        return Network.get(randomIndex);
    }

    public VanillaRandomCallModel(String name){
        noSelfCalls = Configuration.contains(name + "." + PAR_NO_SELFCALLS);
    }

    @Override
    public void generateMapping() {}
    public void nextCycle(Node node, int protocolID) {}

    @Override
    public Object clone() {
        return this;
    }
}
