package network;

import peersim.core.Linkable;
import peersim.core.Node;

public interface RandomLinkable extends Linkable {
    Node getRandomNeighbor();
}
