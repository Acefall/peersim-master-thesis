package messagePassing.randomCallModel;

import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

/**
 * Random phone call model with the constraint that if node u calls v, also v calls u.
 * Every pair is equally likely.
 * */
public class MatchingRandomCallModel implements RandomCallModel{
    private final RandomMatching randomMatching;

    public MatchingRandomCallModel(String name){
        randomMatching = new RandomMatching(CommonState.r);
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
