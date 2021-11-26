package protocols.tsHistMean;

import protocols.AggregationProtocol;
import messagePassing.Message;
import messagePassing.MessagePassing;
import network.RandomLinkable;
import protocols.approximation.Approximation;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;

import java.util.Iterator;

/**
 * Time series approximate histogram mean algorithm inspired by https://doi.org/10.1145/3177922 by Cichon et. al.
 */

public class TsHistMean extends AggregationProtocol implements CDProtocol, Approximation {
    protected static final String PAR_L = "L";
    protected static final String PAR_K = "K";
    protected static final String PAR_m = "m";
    protected static final String PAR_M = "M";


    private String prefix;
    private double[][] histJoin;
    private double[][] histLeave;
    private int a = -1;


    private static int L;
    private static int K;
    private static double m;
    private static double M;

    public TsHistMean(String prefix) {
        L = Configuration.getInt(prefix + "." + PAR_L);
        K = Configuration.getInt(prefix + "." + PAR_K);
        m = Configuration.getDouble(prefix + "." + PAR_m);
        M = Configuration.getDouble(prefix + "." + PAR_M);
        this.prefix = prefix;
        initializeHistogram();
    }

    public void initializeHistogram() {
        histJoin = new double[K][L];
        histLeave = new double[K][L];
        for (int k = 0; k < K; k++){
            for (int l = 0; l < L; l++){
                histJoin[k][l] = Double.MAX_VALUE;
                histLeave[k][l] = Double.MAX_VALUE;
            }
        }
    }


    @Override
    public void nextCycle(Node node, int protocolID) {
        int newInterval = findInterval(getInput());
        if(newInterval != a){
            if(a != -1) {
                minimizeInterval(a, newRandomInterval(), histLeave);
            }
            minimizeInterval(newInterval, newRandomInterval(), histJoin);
            a = newInterval;
        }

        processInboundMessages(node, protocolID);


        // Get a random neighbour
        int linkableID = FastConfig.getLinkable(protocolID);
        RandomLinkable linkable = (RandomLinkable) node.getProtocol(linkableID);
        Node peer = linkable.getRandomNeighbor();
        if (CommonState.getIntTime() % 2 == 0) {
            messagePassing.putOutboundMessage(new TsHistMeanPullRequest(node, peer, protocolID));
        }
    }

    private void processInboundMessages(Node node, int protocolID) {
        // Process Responses
        Iterator<Message> messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            if (message instanceof TsHistMeanPullResponse) {
                TsHistMeanPullResponse response = (TsHistMeanPullResponse) message;
                minimizeMatrix(histJoin, response.getHistJoin());
                minimizeMatrix(histLeave, response.getHistLeave());
                messages.remove();
            }
        }

        // Process Requests
        messages = messagePassing.getInBoundMessages();
        while (messages.hasNext()) {
            Message message = messages.next();
            if (message instanceof TsHistMeanPullRequest) {
                messagePassing.putOutboundMessage(new TsHistMeanPullResponse(
                        node,
                        message.getSender(),
                        protocolID,
                        copyMatrix(histJoin),
                        copyMatrix(histLeave)));

                messages.remove();
            }
        }
    }

    private double[][] copyMatrix(double[][] m){
        double[][] copy =  new double[m.length][];
        for(int i = 0; i < copy.length; i++){
            copy[i] = new double[m[i].length];
            System.arraycopy(m[i], 0, copy[i], 0, copy[i].length);
        }
        return copy;
    }


    public Object clone() {
        TsHistMean tsHistMean = new TsHistMean(prefix);
        tsHistMean.messagePassing = new MessagePassing();
        tsHistMean.initializeHistogram();
        return tsHistMean;
    }

    @Override
    public double getApproximation() {
        double weightedSum = 0;
        double n = 0;
        for (int k = 0; k < K; k++){
            double count = countInterval(k);
            if(true) {
                weightedSum += midPoint(k) * count;
                n += count;
            }
        }
        return weightedSum/n;
    }

    private void minimizeMatrix(double[][] target, double[][] m2){
        for (int k = 0; k < K; k++){
            minimizeInterval(k, m2[k], target);
        }
    }


    private void minimizeInterval(int a, double[] column, double[][] matrix){
        for (int l = 0; l < L; l++){
            matrix[a][l] = Math.min(matrix[a][l], column[l]);
        }
    }

    private double[] newRandomInterval(){
        double[] column = new double[L];
        for (int l = 0; l < L; l++){
            column[l] = expRandom(1);
        }
        return column;
    }

    private double expRandom(double lambda){
        if(lambda <= 0){
            System.err.println("Lambda has to be greater than 0");
            lambda = 1;
        }
        return (-1/lambda)*Math.log(CommonState.r.nextDouble());
    }

    private double intervalWidth(){
        return (M-m)/K;
    }

    private double intervalBegin(int a){
        return m+a*intervalWidth();
    }

    private double intervalEnd(int a){
        return intervalBegin(a)+intervalWidth();
    }

    private double midPoint(int a){
        return intervalBegin(a)+intervalWidth()/2;
    }

    private double countInterval(int a){
        double sumJoin = 0;
        double sumLeave = 0;
        for (int l = 0; l < L; l++){
            sumJoin += histJoin[a][l];
            sumLeave += histLeave[a][l];
        }
        double nJoin = 0;
        double nLeave = 0;
        if(sumJoin > 0){
            nJoin = (L-1)/sumJoin;
        }
        if(sumLeave > 0){
            nLeave = (L-1)/sumLeave;
        }

        return nJoin - nLeave;
    }

    private int findInterval(double x){
        int interval = (int) Math.floor(K*(x -m)/(M-m));
        interval = Math.max(0, interval);
        interval = Math.min(K-1, interval);
        return interval;
    }
}