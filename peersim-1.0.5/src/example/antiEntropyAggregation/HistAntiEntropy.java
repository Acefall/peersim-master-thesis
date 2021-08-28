package example.antiEntropyAggregation;

import approximation.Approximation;
import example.AggregationProtocol;
import example.RandomLinkable;
import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.RandomCallModel;
import messagePassing.randomCallModel.RandomMatching;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

import java.util.Iterator;

public class HistAntiEntropy extends AggregationProtocol implements CDProtocol, Approximation, RequiresLeader {
    protected static final String PAR_K = "K";
    protected static final String PAR_m = "m";
    protected static final String PAR_M = "M";


    private String prefix;
    private Histogram histogram;
    private int a = -1;


    private static int K;
    private static double m;
    private static double M;

    public HistAntiEntropy(String prefix) {
        K = Configuration.getInt(prefix + "." + PAR_K);
        m = Configuration.getDouble(prefix + "." + PAR_m);
        M = Configuration.getDouble(prefix + "." + PAR_M);
        this.prefix = prefix;
        initializeHistogram(false);
    }

    public void initializeHistogram(boolean leader) {
        histogram = new Histogram(K, m, M);
        histogram.initialize(leader);
    }


    @Override
    public void nextCycle(Node node, int protocolID) {
        processInboundMessages(node, protocolID);

        int newInterval = histogram.getBin(getInput());
        if(newInterval != a){
            if(a != -1) {
                histogram.decrementBin(a, 1);
            }
            histogram.incrementBin(newInterval, 1);
            a = newInterval;
        }


        // Get a random neighbour
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomCallModel linkable = (RandomCallModel) node.getProtocol(linkableID);
        Node peer = linkable.getCommunicationPartner(node);

        messagePassing.putOutboundMessage(new HistAntiEntropyMessage(node, peer, protocolID, histogram.clone()));
    }

    private void processInboundMessages(Node node, int protocolID) {
        if(CommonState.getIntTime() > 0) {
            // Because of random matching there is always one and exactly one message for every node
            Iterator<Message> messages = getInBoundMessages();
            HistAntiEntropyMessage message = (HistAntiEntropyMessage) messages.next();
            histogram.exchange(message.getHistogram());
            messages.remove();
        }
    }

    public Object clone() {
        HistAntiEntropy histAntiEntropy = new HistAntiEntropy(prefix);
        histAntiEntropy.messagePassing = new MessagePassing();
        return histAntiEntropy;
    }


    @Override
    public double getApproximation() {
        return histogram.approximatedMean();
    }

    @Override
    public void designateAsLeader() {
        histogram.initialize(true);
    }
}
