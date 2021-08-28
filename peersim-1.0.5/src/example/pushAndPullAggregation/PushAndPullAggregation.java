package example.pushAndPullAggregation;

import approximation.Approximation;
import example.randomCallPull.RandomCallPull;
import example.randomCallPull.RandomCallPullResponse;
import example.randomCallPush.RandomCallPush;
import messagePassing.Message;
import messagePassing.MessagePassing;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PushAndPullAggregation extends RandomCallPull implements CDProtocol, Approximation, PullProtocol {
    /**
     * Parameter that defines the push protocol to interact with.
     */
    private static final String PAR_PUSH_PROT = "pushProtocol";

    /**
     * Protocol identifier, obtained from config property {@link #PAR_PUSH_PROT}.
     */
    private final int pushProtocolID;


    public PushAndPullAggregation(String prefix) {
        super(prefix);
        pushProtocolID = Configuration.getPid(name + "." + PAR_PUSH_PROT);
    }

    private RandomCallPush getPushProtocol(Node node){
        return (RandomCallPush) node.getProtocol(pushProtocolID);
    }

    @Override
    public void processPullCalls(List<PullCall> pullCalls, Node node, int protocolID) {
        RandomCallPush pushProtocol = getPushProtocol(node);

        for (Message pullCall : pullCalls){
            messagePassing.putOutboundMessage(new RandomCallPullResponse(
                    node,
                    pullCall.getSender(),
                    protocolID,
                    pushProtocol.getS(),
                    pushProtocol.getN()));
        }
    }

    public Object clone() {
        PushAndPullAggregation pApAggregation = new PushAndPullAggregation(name);
        pApAggregation.messagePassing = new MessagePassing();
        return pApAggregation;
    }
}
