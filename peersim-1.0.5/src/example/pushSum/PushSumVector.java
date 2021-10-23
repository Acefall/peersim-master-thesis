// Author: Alexander Weinmann uni@aweinmann.de
package example.pushSum;

import approximation.Approximation;
import example.AggregationProtocol;
import example.HasContributions;
import example.pullSum.HasWeight;
import example.pullSum.PullSumVectorResponse;
import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Pull Sum protocol inspired by Push Sum
 * Uses conservation of mass to converge to the true mean.
 */
public class PushSumVector extends AggregationProtocol implements CDProtocol, Approximation, HasWeight, HasContributions {
    protected final String name;
    private Double[] contributions;
    private int protocolID;

    public Double[] getContributions(){
        return contributions;
    }

    public PushSumVector(String prefix) {
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

        if (CommonState.getIntTime() != 0) {
            processInboundMessages(node, protocolID);
        }

        // Get a random neighbour
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        Node peer = linkable.getCommunicationPartner(node);

        Double[] responseContributions =  new Double[Network.size()];
        for (int i = 0; i < Network.size(); i++) {
            responseContributions[i] = contributions[i]/2;
        }

        messagePassing.putOutboundMessage(new PullSumVectorResponse(
                node,
                peer,
                protocolID,
                responseContributions));
        messagePassing.putOutboundMessage(new PullSumVectorResponse(
                node,
                node,
                protocolID,
                responseContributions));

    }


    private void processInboundMessages(Node node, int protocolID) {
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
        PushSumVector pushSumVector = new PushSumVector(name);
        pushSumVector.messagePassing = new MessagePassing();
        return pushSumVector;
    }


    @Override
    public double getApproximation() {
        double dotProduct = 0;
        for (int i = 0; i < Network.size(); i++) {
            PushSumVector protocol = (PushSumVector) Network.get(i).getProtocol(protocolID);
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
