package messagePassing;

import outputs.BufferedLogger;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

import java.io.IOException;
import java.util.Iterator;

/**
 * Counts all messages in the outgoing messages set, sums over of all nodes.
 * */
public class MessageCounter extends BufferedLogger implements Control {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";
    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
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
                it.next();
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
