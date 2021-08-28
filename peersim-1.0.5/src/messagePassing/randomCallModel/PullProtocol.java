package messagePassing.randomCallModel;

import messagePassing.MPProtocol;
import messagePassing.Message;
import peersim.core.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface PullProtocol extends MPProtocol {
    void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID);
}
