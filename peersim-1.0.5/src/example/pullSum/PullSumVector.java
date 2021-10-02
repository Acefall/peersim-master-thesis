// Author: Alexander Weinmann uni@aweinmann.de
package example.pullSum;

import approximation.Approximation;
import approximation.SWApproximation;
import example.AggregationProtocol;
import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Pull Sum protocol inspired by Push Sum
 * Uses conservation of mass to converge to the true mean.
 */
public class PullSumVector extends AggregationProtocol implements CDProtocol, Approximation, PullProtocol, HasWeight {
    protected final String name;
    private Double[] contributions;
    private int protocolID;

    public Double[] getContributions(){
        return contributions;
    }


    public PullSumVector(String prefix) {
        this.name = prefix;
        contributions = new Double[Network.size()];
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        if(CommonState.getIntTime() == 0){
            this.protocolID = protocolID;
            Arrays.fill(contributions, 0.0);
            contributions[(int)node.getID()] = 1.0;
        }

        // Get a random neighbour
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        Node peer = linkable.getCommunicationPartner(node);


        messagePassing.putOutboundMessage(new PullCall(node, peer, protocolID));
        messagePassing.putOutboundMessage(new PullCall(node, node, protocolID));
        if (CommonState.getIntTime() != 0) {
            processResponses();
        }

    }

    private void processResponses() {
        Arrays.fill(contributions, 0.0);
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            if (message instanceof PullSumVectorResponse) {
                PullSumVectorResponse response = (PullSumVectorResponse) message;
                for (int i = 0; i < Network.size(); i++) {
                    contributions[i] += response.getContributions()[i];
                }
                messages.remove();
            }
        }
    }

    public Object clone() {
        PullSumVector pullSum = new PullSumVector(name);
        pullSum.messagePassing = new MessagePassing();
        return pullSum;
    }

    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        Double[] responseContributions =  new Double[Network.size()];
        for (int i = 0; i < Network.size(); i++) {
            responseContributions[i] = contributions[i]/pullCalls.size();
        }
        for (Message pullCall : pullCalls) {
            messagePassing.putOutboundMessage(new PullSumVectorResponse(
                    node,
                    pullCall.getSender(),
                    protocolID,
                    responseContributions));
        }
    }

    @Override
    public double getApproximation() {
        double dotProduct = 0;
        for (int i = 0; i < Network.size(); i++) {
            PullSumVector protocol = (PullSumVector) Network.get(i).getProtocol(protocolID);
            dotProduct += protocol.getInput() * contributions[i];
        }
        return dotProduct/getWeight();
    }

    @Override
    public double getWeight() {
        double weight = 0;
        for (int i = 0; i < contributions.length; i++) {
            weight += contributions[i];
        }
        return  weight;
    }
}
