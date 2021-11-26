package protocols.adaptiveSampling;

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
import protocols.approximation.Approximation;

import java.util.Iterator;
import java.util.List;

public class ActiveAndRelaxed extends Sampling implements CDProtocol, Approximation, PullProtocol {
    private double marginOfError = 0.05;
    private int maxWaitTime = 32;

    private static String PAR_SAMPLE_SIZE = "sampleSize";
    private final int sampleSize;

    private static String PAR_MAX_SAMPLING_RATE = "maxSamplingRate";
    private final int maxSamplingRate;

    private boolean isAlert = true;
    private boolean justWokenUp = false;
    private int largestWakeUpId = 0;
    private double lastAlertApproximation = 0;
    private double lastApproximation = 0;
    private int lastAlertTime = 0;
    private int nextWaitTime = 1;
    private int nextSampleInRound = 0;

    public ActiveAndRelaxed(String name) {
        super(name);
        maxSamplingRate = Configuration.getInt(name + "." + PAR_MAX_SAMPLING_RATE);
        sampleSize = Configuration.getInt(name + "." + PAR_SAMPLE_SIZE);
    }

    private void processResponses(Node node, int protocolID) {
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            if(message instanceof WakeUpCall){
                wakeUpFromCall(node, protocolID, (WakeUpCall) message);

                break;
            }
        }

        super.processResponses();
    }

    protected void randomWakeUpCalls(Node node, int protocolID, int numberOfWakeUps, int largestWakeUpId) {
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        for (int i = 0; i < numberOfWakeUps; i++) {
            Node peer = linkable.getCommunicationPartner(node);
            messagePassing.putOutboundMessage(new WakeUpCall(node, peer, protocolID, largestWakeUpId));
        }
    }

    private void wakeUpFromCall(Node node, int protocolID, WakeUpCall wakeUpCall){
        if(wakeUpCall.getWakeUpId() > largestWakeUpId){
            if(!isAlert) {
                relaxedToAlerted(node, protocolID);
                justWokenUp = true;
            }
            largestWakeUpId = wakeUpCall.getWakeUpId();
            randomWakeUpCalls(node, protocolID, 2, largestWakeUpId);
        }
    }

    private void wakeUpFromChange(Node node, int protocolID){
        largestWakeUpId++;
        randomWakeUpCalls(node, protocolID, 2, largestWakeUpId);
        relaxedToAlerted(node, protocolID);
    }

    private void relaxedToAlerted(Node node, int protocolID){
        isAlert = true;
    }

    private void alertedToRelaxed(){
        nextWaitTime = 1;
        isAlert = false;
        nextSampleInRound = CommonState.getIntTime();
    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        processResponses(node, protocolID);

        double newApproximation = getApproximation();
        double relativeErrorLastAlert = Math.abs((newApproximation - lastAlertApproximation)/lastAlertApproximation);

        if(! justWokenUp){
            if(relativeErrorLastAlert > marginOfError){
                wakeUpFromChange(node, protocolID);

            }else if(isAlert){
                alertedToRelaxed();
            }
        }else{
            justWokenUp = false;
        }


        if(isAlert){
            double relativeErrorLastEstimate = Math.abs((newApproximation - lastApproximation)/lastApproximation);

            requestSamples(node, protocolID, (int) Math.floor(
                    Math.min(1,
                            Math.max(relativeErrorLastEstimate, relativeErrorLastAlert))
                    ) * maxSamplingRate
            );


            lastAlertApproximation = newApproximation;
            lastAlertTime = CommonState.getIntTime();
        }
        lastApproximation = newApproximation;

        if(!isAlert & CommonState.getIntTime() >= nextSampleInRound){
            requestSamples(node, protocolID, 1);

            nextSampleInRound = CommonState.getIntTime() + CommonState.r.nextInt(nextWaitTime) + nextWaitTime;
            nextWaitTime = Math.min(maxWaitTime, nextWaitTime * 2);
        }
    }

    @Override
    public Object clone() {
        ActiveAndRelaxed protocol = new ActiveAndRelaxed(name);
        protocol.messagePassing = new MessagePassing();
        return protocol;
    }

    @Override
    public double getApproximation() {
        return super.getApproximation(sampleSize);
    }

    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        for (PullCall pullCall : pullCalls){
            if(isAlert) {
                messagePassing.putOutboundMessage(
                        new SamplingMessage(
                                node,
                                pullCall.getSender(),
                                protocolID,
                                new Sample(CommonState.getIntTime(), getInput())
                        )
                );
            }else{
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
}
