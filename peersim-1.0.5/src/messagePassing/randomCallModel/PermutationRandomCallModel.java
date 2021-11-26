package messagePassing.randomCallModel;

import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.util.RandPermutation;

/**
 * Random phone call model with the constraint that every node has exactly one incoming call.
 * This constraint represents a permutation. Every permutation is equally likely.
 * */
public class PermutationRandomCallModel implements RandomCallModel{
    private final RandPermutation permutation;

    @Override
    public Node getCommunicationPartner(Node caller) {
        int permutedIndex = permutation.get(caller.getIndex());
        return Network.get(permutedIndex);
    }

    public PermutationRandomCallModel(String name){
        permutation = new RandPermutation(Network.size(), CommonState.r);
    }

    @Override
    public void generateMapping() {
        permutation.setPermutation(Network.size());
    }


    @Override
    public Object clone() {
        return this;
    }
}
