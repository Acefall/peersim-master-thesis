package messagePassing.randomCallModel;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

public class VanillaRandomCallModel implements RandomCallModel{
    private static final String PAR_NO_SELFCALLS = "noSelfCalls";
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
