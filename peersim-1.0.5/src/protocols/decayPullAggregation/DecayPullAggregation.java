package protocols.decayPullAggregation;

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
import timeseries.EpochProtocol;

import java.util.Iterator;
import java.util.List;

public class DecayPullAggregation extends AggregationProtocol implements EpochProtocol, CDProtocol, Approximation, PullProtocol {
    private final String name;
    private static final String PAR_HISTORY_WEIGHT = "historyWeight";

    private final double historyWeight;
    private double estimate = 0;

    public DecayPullAggregation(String name){
        this.name = name;
        historyWeight = Configuration.getDouble(name + "." + PAR_HISTORY_WEIGHT);
    }

    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        for (PullCall pullCall : pullCalls){
            messagePassing.putOutboundMessage(
                    new EstimationMessage(
                            node,
                            pullCall.getSender(),
                            protocolID,
                            estimate
                    )
            );
        }
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        if(CommonState.getIntTime() > 0) {
            estimate = historyWeight * estimate + (1 - historyWeight) * getInput();
        }

        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        Node peer = linkable.getCommunicationPartner(node);
        messagePassing.putOutboundMessage(new PullCall(node, peer, protocolID));
    }

    @Override
    public Object clone() {
        DecayPullAggregation protocol = new DecayPullAggregation(name);
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

    @Override
    public void processInboundMessages(Node node, int protocolID) {
        double sum = estimate;
        int count = 1;
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            EstimationMessage estimationMessage = (EstimationMessage) messages.next();
            sum += estimationMessage.getEstimation();
            count++;
            messages.remove();
        }

        estimate = sum/count;

    }
}
