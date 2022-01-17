package protocols.baseline;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import protocols.AggregationProtocol;

public class IgnoreChangesController implements Control {
    private static final String PAR_PROT = "protocol";
    private final int protocolID;


    public IgnoreChangesController(String name){
        protocolID = Configuration.getPid(name + "." + PAR_PROT);
    }



    @Override
    public boolean execute() {
        if(CommonState.getIntTime() == 0){
            double firstGroundTruth = 0;
            for (int i = 0; i < Network.size(); i++) {
                IgnoreChanges protocol = (IgnoreChanges) Network.get(i).getProtocol(protocolID);
                firstGroundTruth += protocol.getInput();
            }
            firstGroundTruth = firstGroundTruth/Network.size();
            for (int i = 0; i < Network.size(); i++) {
                IgnoreChanges protocol = (IgnoreChanges) Network.get(i).getProtocol(protocolID);
                protocol.setFirstGroundTruth(firstGroundTruth);
            }
        }
        return false;
    }
}
