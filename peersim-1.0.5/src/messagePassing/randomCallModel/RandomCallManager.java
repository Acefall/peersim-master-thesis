package messagePassing.randomCallModel;

import messagePassing.MPProtocol;
import messagePassing.Message;
import messagePassing.MessageManager;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.Protocol;

import java.util.*;

/**
 * Handles the message passing amongst the nodes.
 * Collects push calls and pull calls and delivers them to the designated receiver.
 * */
public class RandomCallManager extends MessageManager {
    /**
     * Parameter that defines whether RPULL is used or not.
     */
    private static final String PAR_RPULL = "rpull";
    /** Whether RPULL is used or not, obtained from config property {@link #PAR_RPULL}. */
    private final boolean rpull;

    private final Map<Node, List<PullCall>> pullCalls = new HashMap<>();

    public RandomCallManager(String name) {
        super(name);
        rpull = Configuration.contains(name + "." + PAR_RPULL);
        resetPullCalls();
    }

    public boolean execute() {
        collectPullCalls();
        processPullCalls();
        resetPullCalls();
        return super.execute();
    }

    private void resetPullCalls(){
        pullCalls.clear();
        for (int i = 0; i < Network.size(); i++) {
            pullCalls.put(Network.get(i), new ArrayList<>());
        }
    }

    private void processPullCalls(){
        for (int i = 0; i < Network.size(); i++) {
            Node callee = Network.get(i);
            Protocol protocol =  callee.getProtocol(pid);
            if(protocol instanceof PullProtocol){
                PullProtocol pullProtocol = (PullProtocol) protocol;

                if(callee.isUp()){
                    if(rpull && pullCalls.get(callee).size() > 0){
                        List<PullCall> restrictedPullCalls = new ArrayList<>();
                        int randomIndex = CommonState.r.nextInt(pullCalls.get(callee).size());
                        PullCall randomPullCall = pullCalls.get(callee).get(randomIndex);
                        restrictedPullCalls.add(randomPullCall);
                        pullProtocol.processPullCalls(restrictedPullCalls, callee, pid);

                    }else{
                        pullProtocol.processPullCalls(pullCalls.get(callee), callee, pid);
                    }
                }
            }

        }
    }

    private void collectPullCalls(){
        for (int i = 0; i < Network.size(); i++) {
            MPProtocol messagePassing = (MPProtocol) Network.get(i).getProtocol(pid);
            Iterator<Message> outBoundMessages = messagePassing.getOutBoundMessages();
            while (outBoundMessages.hasNext()) {
                Message m = outBoundMessages.next();
                if (m instanceof PullCall){
                    PullCall pullCall = (PullCall) m;
                    pullCalls.get(pullCall.getReceiver()).add(pullCall);
                    outBoundMessages.remove();
                }
            }
        }
    }
}
