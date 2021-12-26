package protocols.adaptiveSampling;

import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.RandomCallModel;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;
import protocols.AggregationProtocol;
import protocols.sampling.CircularBuffer;

import java.util.*;

public class Sampling extends AggregationProtocol {
    protected final String name;
    protected final CircularBuffer<Sample> samples;

    public Sampling(String name, int bufferSize) {
        this.name = name;
        samples = new CircularBuffer<>(bufferSize);
    }



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

    protected void processResponses(){
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

    protected void requestSamples(Node node, int protocolID, int numberOfSamples) {
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        for (int i = 0; i < numberOfSamples; i++) {
            Node peer = linkable.getCommunicationPartner(node);
            messagePassing.putOutboundMessage(new PullCall(node, peer, protocolID));
        }
    }


    public double getApproximation(int sampleSize) {
        if(samples.count() == 0){
            return getInput();
        }

        int sampleCount = 0;
        double sum = 0;

        Sample sample;
        Iterator<Sample> it = samples.iterator();

        while (it.hasNext()){
            sample = it.next();
            sum += sample.getValue();
            sampleCount++;

            if(sampleCount >= sampleSize){
                break;
            }
        }

        return sum / sampleCount;
    }
}
