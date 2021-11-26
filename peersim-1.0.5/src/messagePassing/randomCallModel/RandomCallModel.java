package messagePassing.randomCallModel;

import peersim.core.Node;
import peersim.core.Protocol;

public interface RandomCallModel extends Protocol {
    Node getCommunicationPartner(Node caller);
    void generateMapping();
}
