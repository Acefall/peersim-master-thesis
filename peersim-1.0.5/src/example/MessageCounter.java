package example;

import messagePassing.MPProtocol;
import messagePassing.Message;
import messagePassing.randomCallModel.PullCall;
import messagePassing.randomCallModel.PullProtocol;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

import java.io.IOException;
import java.util.Iterator;

public class MessageCounter extends BufferedLogger implements Control {
    private static final String PAR_PROT = "protocol";
    private final int protocolID;

    public MessageCounter(String name) {
        super(name);
        protocolID = Configuration.getPid(name + "." + PAR_PROT);
    }

    @Override
    protected void writeToFile() {

        double countMessages = 0;
        for (int i = 0; i < Network.size(); i++) {
            MPProtocol protocol = (MPProtocol) Network.get(i).getProtocol(protocolID);
            for (Iterator<Message> it = protocol.getInBoundMessages(); it.hasNext(); ) {
                Message message = it.next();
                    countMessages++;
            }
        }
        try{
            bufferedWriter.write(String.valueOf(countMessages));
            bufferedWriter.newLine();
        }catch(IOException e){
            System.out.println("Could not write to file.");
        }

    }
}
