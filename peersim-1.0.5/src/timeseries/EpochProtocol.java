package timeseries;

import peersim.core.Node;

public interface EpochProtocol {
    void processInboundMessages(Node node, int protocolID);
}
