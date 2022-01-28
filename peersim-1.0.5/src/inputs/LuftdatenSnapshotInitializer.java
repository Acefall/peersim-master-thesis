// Author: Alexander Weinmann uni@aweinmann.de
package inputs;

import protocols.AggregationProtocol;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import timeseries.IDataProvider;


import java.time.LocalDateTime;

/**
 * Uses a data provider to get the inputs to nodes in the frist round.
 */

public class LuftdatenSnapshotInitializer implements Control {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";
    /**
     * Parameter that defines the data provider to use.
     */
    private static final String PAR_DATA_PROVIDER = "dataProvider";
    /**
     * Parameter that defines the dae time at which to take the snapshot
     */
    private static final String PAR_DTM = "dtm";

    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int protocolID;

    /** Data Provider, obtained from config property {@link #PAR_DATA_PROVIDER}. */
    private final IDataProvider dataProvider;

    /** Datetime in UTC on which to take the snapshot,
     * obtained from  config property {@link #PAR_DTM}.*/
    private final LocalDateTime dtm;


    public LuftdatenSnapshotInitializer(String name) {
        protocolID = Configuration.getPid(name + "." + PAR_PROT);
        dataProvider = (IDataProvider) Configuration.getInstance(name + "." + PAR_DATA_PROVIDER);
        dtm = LocalDateTime.parse(Configuration.getString(name + "." + PAR_DTM));
    }

    public boolean execute() {
        var data = dataProvider.nodeValuesAt(dtm);

        for (int i = 0; i < Network.size(); i++) {
            AggregationProtocol protocol = (AggregationProtocol) Network.get(i).getProtocol(protocolID);
            protocol.setInput(data.get(i));
        }

        return false;
    }
}
