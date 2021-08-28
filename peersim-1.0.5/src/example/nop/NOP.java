package example.nop;

import example.AggregationProtocol;
import peersim.cdsim.CDProtocol;
import peersim.core.Node;

public class NOP extends AggregationProtocol implements CDProtocol {
    @Override
    public void nextCycle(Node node, int protocolID) {

    }

    public NOP(String name){}

    @Override
    public Object clone() {
        return new NOP("");
    }
}
