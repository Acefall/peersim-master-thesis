package messagePassing;

import outputs.BufferedLogger;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Protocol;

import java.io.IOException;
import java.util.Iterator;

public class PullRequestCounter extends BufferedLogger implements Control {
    private static final String PAR_PROT = "protocol";
    private final int protocolID;

    public PullRequestCounter(String name) {
        super(name);
        protocolID = Configuration.getPid(name + "." + PAR_PROT);
    }

    @Override
    protected void writeToFile() {
        double countPullCalls = 0;
        for (int i = 0; i < Network.size(); i++) {
            Protocol protocol = Network.get(i).getProtocol(protocolID);
            if(protocol instanceof PullProtocol) {
                PullProtocol pullProtocol = (PullProtocol) Network.get(i).getProtocol(protocolID);
                for (Iterator<Message> it = pullProtocol.getOutBoundMessages(); it.hasNext(); ) {
                    Message message = it.next();
                    if (message instanceof PullCall) {
                        countPullCalls++;
                    }
                }
            }
        }
        try{
            bufferedWriter.write(String.valueOf(countPullCalls));
            bufferedWriter.newLine();
        }catch(IOException e){
            System.out.println("Could not write to file.");
        }

    }
}
