package protocols.adaptiveSampling;

import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;
import protocols.AggregationProtocol;
import protocols.approximation.Approximation;

import java.util.*;

public class MeanPredictsMean extends AggregationProtocol implements CDProtocol, Approximation, PullProtocol {
    private final String name;
    private final List<Sample> samples;

    private final int goodSampleSize = 100;

    public MeanPredictsMean(String name){
        this.name = name;
        samples = new ArrayList<>();
    }

    private void processResponses(){
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            if(message instanceof SamplingMessage){
                SamplingMessage samplingMessage = (SamplingMessage) message;
                Sample sample = samplingMessage.getSample();
                samples.add(sample);
            }
            messages.remove();
        }
    }

    private double mean(Set<Sample> samples){
        double sum = 0;
        for (Sample sample : samples){
            sum += sample.getValue();
        }

        return sum / samples.size();
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        if(CommonState.getIntTime() != 0){
            processResponses();
        }

        int numSamplesToRequest;

        // In the first round
        if(CommonState.getIntTime() == 0){
            numSamplesToRequest = 2 * goodSampleSize;
        }
        else{
            Set<Sample> newSamples = new HashSet<>();
            int i;
            for (i = samples.size()-1; i >= samples.size()-goodSampleSize; i--){
                newSamples.add(samples.get(i));
            }
            Set<Sample> oldSamples = new HashSet<>();
            for (; i >= samples.size()-2*goodSampleSize; i--){
                oldSamples.add(samples.get(i));
            }

            double absMeanDerivative = Math.abs(mean(newSamples) - mean(oldSamples));
            numSamplesToRequest = (int) Math.ceil(absMeanDerivative * goodSampleSize);
            numSamplesToRequest = Math.max(1, Math.min(goodSampleSize, numSamplesToRequest));
        }

        for (int i = 0; i < numSamplesToRequest; i++) {
            int linkableID = FastConfig.getLinkable(protocolID);
            RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
            Node peer = linkable.getCommunicationPartner(node);
            messagePassing.putOutboundMessage(new PullCall(node, peer, protocolID));
        }
    }

    @Override
    public Object clone() {
        MeanPredictsMean protocol = new MeanPredictsMean(name);
        protocol.messagePassing = new MessagePassing();
        return protocol;
    }

    @Override
    public double getApproximation() {
        double sum = 0.0;

        for (int i = 0; i < goodSampleSize & i < samples.size(); i++){
            sum += samples.get(samples.size()-i-1).getValue();
        }

        return sum / goodSampleSize;
    }

    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        for (PullCall pullCall : pullCalls){
            messagePassing.putOutboundMessage(
                    new SamplingMessage(
                            node,
                            pullCall.getSender(),
                            protocolID,
                            new Sample(CommonState.getIntTime(), getInput())
                    )
            );
        }
    }
}
