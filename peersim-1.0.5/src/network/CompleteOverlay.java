package network;

import peersim.config.Configuration;
import peersim.core.*;


/**
 * Provides a complete graph. Every node is connected to every other node.
 * A node is also connected to it self.
 * */
public class CompleteOverlay implements Protocol, RandomLinkable {
    private final String prefix;
    private final String model;
    private static final String PAR_MODEL = "model";

    public CompleteOverlay(String prefix){
        this.prefix = prefix;
        model = Configuration.getString(prefix + "." + PAR_MODEL);
    }

    @Override
    public void onKill() {

    }

    @Override
    public int degree() {
        return Network.size();
    }

    @Override
    public Node getNeighbor(int i) {
        return Network.get(i);
    }

    @Override
    public boolean addNeighbor(Node neighbour) {
        return false;
    }

    @Override
    public boolean contains(Node neighbor) {
        return true;
    }

    @Override
    public void pack() {

    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Node getRandomNeighbor() {
        return getNeighbor(CommonState.r.nextInt(degree()));
    }
}
