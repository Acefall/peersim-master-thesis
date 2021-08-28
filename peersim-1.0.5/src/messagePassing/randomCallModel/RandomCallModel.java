package messagePassing.randomCallModel;

import example.RandomLinkable;
import peersim.cdsim.CDProtocol;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.core.Protocol;

public interface RandomCallModel extends Protocol {
    Node getCommunicationPartner(Node caller);
    void generateMapping();
}
