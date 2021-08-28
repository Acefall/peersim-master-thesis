package example.loadBalance2;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

import java.text.CompactNumberFormat;

public class ResetQuota2 implements Control {
    // ------------------------------------------------------------------------
    // Parameters
    // ------------------------------------------------------------------------
    private static final String PAR_PROT = "protocol";

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    /** Value obtained from config property {@link #PAR_PROT}*/
    private final int protocolID;

    // ------------------------------------------------------------------------
    // Initialization
    // ------------------------------------------------------------------------
    public ResetQuota2(String prefix) {
        protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
    }



    @Override
    public boolean execute() {
        for (int i = 0; i < Network.size(); i++) {
            ((BasicBalance2) Network.get(i).getProtocol(protocolID)).resetQuota();
        }
        return  false;
    }
}
