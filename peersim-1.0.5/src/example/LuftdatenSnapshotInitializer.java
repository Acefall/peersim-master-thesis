// Author: Alexander Weinmann uni@aweinmann.de
package example;

import approximation.Approximation;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import timeseries.IDataProvider;


import java.time.LocalDateTime;

/**
 * Uses a data provider to get the inputs to nodes for every round. Updates the inputs of the nodes using that data.
 */

public class LuftdatenSnapshotInitializer implements Control {
    private static final String PAR_PROT = "protocol";
    private static final String PAR_DATA_PROVIDER = "dataProvider";
    private static final String PAR_DTM = "dtm";

    /**
     * Protocol to operate on.
     */
    private final int protocolID;

    private final IDataProvider dataProvider;
    /**
     * Date time in UTC
     */
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
