package protocols.pullSum;

import outputs.BufferedLogger;
import peersim.config.Configuration;
import peersim.core.Network;

import java.io.IOException;

/**
 * Logs the weight of every node individually in every round.
 * */
public class WeightLogger extends BufferedLogger {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";
    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int protocolID;

    public WeightLogger(String name) {
        super(name);
        protocolID = Configuration.getPid(name + "." + PAR_PROT);
    }

    @Override
    protected void writeToFile() {
        try {
            for (int i = 0; i < Network.size(); i++) {
                HasWeight protocol = (HasWeight) Network.get(i).getProtocol(protocolID);
                bufferedWriter.write(String.valueOf(protocol.getWeight()));
                bufferedWriter.write(",");
            }
            bufferedWriter.newLine();
        }catch(IOException e){
            System.out.println("Could not write to file.");
        }
    }
}
