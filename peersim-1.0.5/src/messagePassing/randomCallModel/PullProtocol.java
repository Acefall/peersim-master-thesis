package messagePassing.randomCallModel;

import messagePassing.MPProtocol;
import peersim.core.Node;

import java.util.List;

public interface PullProtocol extends MPProtocol {
    void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID);
}
