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
import timeseries.EpochProtocol;

import java.util.Iterator;
import java.util.List;
/**
 * Protocol that operates in two states. Awake and asleep. IN the asleep state samples with exponential backoff.
 * In the awake state samples based on the relative change in estimate.
 * When detecting a large relative change, wakes up and wakes up other nodes aswell.
 * */
public class ActiveAndRelaxed extends Sampling implements EpochProtocol, CDProtocol, Approximation, PullProtocol {
    private double marginOfError;
    private int maxWaitTime;

    private static String PAR_MAX_WAIT_TIME = "maxWaitTime";
    private static String PAR_MARGIN_OF_ERROR = "marginOfError";

    private static String PAR_MAX_SAMPLING_RATE = "maxSamplingRate";
    private final int maxSamplingRate;

    private boolean isAlert = true;
    private boolean justWokenUp = false;
    private int largestWakeUpId = 0;
    private double lastAlertApproximation = 0;
    private double lastApproximation = 0;
    private int nextWaitTime = 1;
    private int nextSampleInRound = 0;

    public ActiveAndRelaxed(String name) {
        super(name, Configuration.getInt(name + "." + PAR_MAX_SAMPLING_RATE));
        maxSamplingRate = Configuration.getInt(name + "." + PAR_MAX_SAMPLING_RATE);
        maxWaitTime = Configuration.getInt(name + "." + PAR_MAX_WAIT_TIME);
        marginOfError = Configuration.getDouble(name + "." + PAR_MARGIN_OF_ERROR);
    }

    public void processInboundMessages(Node node, int protocolID) {
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            if(message instanceof WakeUpCall){
                wakeUpFromCall(node, protocolID, (WakeUpCall) message);

                break;
            }
        }

        super.processInboundMessages(node, protocolID);
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
                     * maxSamplingRate)
            );


            lastAlertApproximation = newApproximation;
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
        return super.getApproximation(maxSamplingRate);
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
