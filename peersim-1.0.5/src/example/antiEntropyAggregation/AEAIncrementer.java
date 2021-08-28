package example.antiEntropyAggregation;

import peersim.cdsim.CDState;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

public class AEAIncrementer implements Control {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";

    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int protocolID;

    public AEAIncrementer(String prefix) {
        protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
    }



    @Override
    public boolean execute() {

        if (CDState.getCycle() > 0 && CDState.getCycle() % 300 ==  0) {
        //if(CDState.getCycle() == 150){
            //incrementRandom(0.5);
            //incrementSingle(0, 1);
            incrementAll(1);
        }
        return false;
    }

    private void incrementRandom(double fraction){
        double increments = Network.size()*fraction;
        for (int i = 0; i < increments; i++) {
            int randomIndex = CDState.r.nextInt(Network.size());
            incrementSingle(randomIndex, 1);
        }
    }

    private void incrementSingle(int index, int increment){
        AntiEntropyAggregation aeaProtocol = (AntiEntropyAggregation) Network.get(index).getProtocol(protocolID);
        aeaProtocol.scheduleIncrement(increment);
    }

    private void incrementAll(int increment){
        for (int i = 0; i < Network.size(); i++) {
            incrementSingle(i, 1);
        }
    }
}
