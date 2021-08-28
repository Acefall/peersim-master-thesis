package messagePassing.randomCallModel;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;


public class MatchingRandomCallModel implements RandomCallModel{
    private RandomMatching randomMatching;

    public MatchingRandomCallModel(String name){
        randomMatching = new RandomMatching(Network.size(), CommonState.r);
        randomMatching.generateMatching(Network.size());
    }

    @Override
    public Node getCommunicationPartner(Node caller) {
        int randomMatchedIndex = randomMatching.get(caller.getIndex());
        return Network.get(randomMatchedIndex);
    }

    @Override
    public void generateMapping() {
        randomMatching.generateMatching(Network.size());
    }

    @Override
    public Object clone() {
        return this;
    }

}
