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

public class ExponentialVariance extends AggregationProtocol implements CDProtocol, Approximation, PullProtocol {
    private final String name;
    private final List<Sample> samples;
    private final List<MeanBin> meanBins;

    private final int window = 5;

    public ExponentialVariance(String name){
        this.name = name;
        samples = new ArrayList<>();
        meanBins = new ArrayList<>();
        for (int i = 0; i < window; i++) {
            meanBins.add(new MeanBin(i));
        }
    }

    private void processResponses(){
        meanBins.add(new MeanBin(CommonState.getIntTime()-1 + window));

        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            if(message instanceof SamplingMessage){
                SamplingMessage samplingMessage = (SamplingMessage) message;
                Sample sample = samplingMessage.getSample();

                samples.add(sample);

                int lowerEnd = Math.max(0, sample.getTime()-window);
                int upperEnd = Math.min(meanBins.size()-1, sample.getTime()+window);
                for (int i = lowerEnd; i <= upperEnd; i++) {
                    meanBins.get(i).addSample(sample);
                }
            }
            messages.remove();
        }
    }


    /**
     * Returns a list of increasing powers of two minus one.
     * The last element is at most as large as size.
     * */
    private List<Integer> breaks(int size){
        List<Integer> breaks = new ArrayList<>();
        if(size <= 0){
            return breaks;
        }

        int largestPossibleBreak = (int) Math.ceil(Math.log(size)/Math.log(2))-1;
        for (int i = 0; i <= largestPossibleBreak; i++) {
            breaks.add((int) Math.pow(2, i)-1);
        }
        breaks.add(Math.min(size, (int) Math.pow(2, largestPossibleBreak))-1);

        return breaks;
    }

    /**
     * Calculates the mean weighted variance of the bins.
     * Which bins are included in the weighted variance is given by the breaks.
     * */
    private double meanWeightedVariance(){
        double sumVariance = 0;
        Set<MeanBin> binsToProcess = new HashSet<>();

        List<Integer> breaks = breaks(meanBins.size());
        ListIterator<Integer> breaksIt = breaks.listIterator();
        int nextBreak = breaksIt.next();

        for (int i = meanBins.size()-1; i > 0 ; i--) {
            MeanBin meanBin = meanBins.get(i);
            if(meanBin.getSampleCount() > 0) {
                binsToProcess.add(meanBin);
            }
            if (i == meanBins.size()-1-nextBreak){
                nextBreak = breaksIt.next();
                sumVariance += WeightedVariance.getWeightedVariance(binsToProcess);
            }
        }

        return sumVariance/breaks.size();
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        if(CommonState.getIntTime() != 0){
            processResponses();
        }

        double meanWeightedVariance = meanWeightedVariance();

        int numSamplesToRequest = (int) Math.max(Math.ceil(meanWeightedVariance)/17, 1);

        for (int i = 0; i < numSamplesToRequest; i++) {
            int linkableID = FastConfig.getLinkable(protocolID);
            RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
            Node peer = linkable.getCommunicationPartner(node);
            messagePassing.putOutboundMessage(new PullCall(node, peer, protocolID));
        }
    }

    @Override
    public Object clone() {
        ExponentialVariance protocol = new ExponentialVariance(name);
        protocol.messagePassing = new MessagePassing();
        return protocol;
    }

    @Override
    public double getApproximation() {
        // take the sample which are not older than 25 timesteps
        double sum = 0.0;
        int sampleCount = 0;

        ListIterator<Sample> li = samples.listIterator(samples.size());

        while(li.hasPrevious()){
            Sample sample = li.previous();

            if(sample.getTime() >= CommonState.getTime()-1-25){
                sum += sample.getValue();
                sampleCount++;
            }
            else{
                break;
            }
        }

        return sum/sampleCount;
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
