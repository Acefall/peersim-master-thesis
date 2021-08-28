package example.antiEntropyAggregation;

import approximation.Approximation;
import example.AggregationProtocol;
import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;

import java.util.Iterator;

public class AntiEntropyAggregation extends AggregationProtocol implements CDProtocol, Approximation, RequiresLeader {
    private int toBeIncremented = 0;
    public AntiEntropyIncrementer incrementer;

    @Override
    public double getInput() {
        return incrementer.getLocalCount();
    }

    private String name;

    public AntiEntropyAggregation(String name) {}

    @Override
    public double getApproximation() {
        return incrementer.getIncrements();
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        if(CommonState.getIntTime() > 0) {
            // Because of random matching there is always one and exactly one message for every node
            Iterator<Message> messages = getInBoundMessages();
            AntiEntropyAggregationMessage message = (AntiEntropyAggregationMessage) messages.next();
            incrementer.exchange(message.getIncrementer());
            messages.remove();
        }

        if(toBeIncremented > 0){
            increment(toBeIncremented);
            toBeIncremented = 0;
        }

        // Get a random neighbour
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        Node peer = linkable.getCommunicationPartner(node);

        messagePassing.putOutboundMessage(new AntiEntropyAggregationMessage(node, peer, protocolID, incrementer.clone()));
    }

    public void scheduleIncrement(int increment){
        toBeIncremented = increment;
    }

    private void increment(int increment){
        incrementer.increment(increment);
    }

    @Override
    public Object clone() {
        AntiEntropyAggregation antiEntropyAggregation = new AntiEntropyAggregation(name);
        antiEntropyAggregation.messagePassing = new MessagePassing();
        antiEntropyAggregation.incrementer = new AntiEntropyIncrementer();
        return antiEntropyAggregation;
    }

    public void designateAsLeader(){
        incrementer.setLeader();
    }
}
