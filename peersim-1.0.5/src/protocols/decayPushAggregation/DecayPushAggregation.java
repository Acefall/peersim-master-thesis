package protocols.decayPushAggregation;

import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;
import protocols.AggregationProtocol;
import protocols.approximation.Approximation;
import protocols.decayPullAggregation.EstimationMessage;
import protocols.pushSum.PushMessage;

import java.util.Iterator;
import java.util.List;

public class DecayPushAggregation extends AggregationProtocol implements CDProtocol, Approximation {
    private final String name;
    private static final String PAR_HISTORY_WEIGHT = "historyWeight";

    private final double historyWeight;
    private double estimate = 0;

    public DecayPushAggregation(String name){
        this.name = name;
        historyWeight = Configuration.getDouble(name + "." + PAR_HISTORY_WEIGHT);
    }

    private void processInboundMessages(Node node, int protocolID) {
        double sum = estimate;
        int count = 1;
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            EstimationMessage estimationMessage = (EstimationMessage) messages.next();
            sum += estimationMessage.getEstimation();
            count++;
            messages.remove();
        }

        estimate = historyWeight * (sum / count) + (1 - historyWeight) * getInput();
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        if(CommonState.getIntTime() > 0) {
            processInboundMessages(node, protocolID);
        }

        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        Node peer = linkable.getCommunicationPartner(node);
        messagePassing.putOutboundMessage(new EstimationMessage(node, peer, protocolID, estimate));
    }

    @Override
    public Object clone() {
        DecayPushAggregation protocol = new DecayPushAggregation(name);
        protocol.messagePassing = new MessagePassing();
        return protocol;
    }

    @Override
    public double getApproximation() {
        if(CommonState.getIntTime() == 0){
            return getInput();
        }
        else {
            return estimate;
        }
    }

    @Override
    public void setInput(double input) {
        if(CommonState.getIntTime() == 0){
            estimate = input;
        }
        super.setInput(input);
    }
}
