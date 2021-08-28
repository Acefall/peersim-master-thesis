package messagePassing.randomCallModel;

import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.util.RandPermutation;

public class PermutationRandomCallModel implements RandomCallModel{
    private RandPermutation permutation;

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
