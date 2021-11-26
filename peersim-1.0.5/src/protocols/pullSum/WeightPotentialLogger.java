package protocols.pullSum;

import outputs.BufferedLogger;
import peersim.config.Configuration;
import peersim.core.Network;

import java.io.IOException;


/**
 * Logs the weight potential of every node individually in every round.
 * */
public class WeightPotentialLogger extends BufferedLogger {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";
    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int protocolID;

    public WeightPotentialLogger(String name) {
        super(name);
        protocolID = Configuration.getPid(name + "." + PAR_PROT);
    }

    protected void writeToFile() {
        try {
            for (int i = 0; i < Network.size(); i++) {
                PullSumVector protocol = (PullSumVector) Network.get(i).getProtocol(protocolID);
                bufferedWriter.write(String.valueOf(potential(protocol.getContributions())));
                bufferedWriter.write(",");
            }
            bufferedWriter.newLine();
        }catch(IOException e){
            System.out.println("Could not write to file.");
        }
    }

    private double potential(Double[] contributions){
        double potential = 0;
        for (Double contribution : contributions) {
            potential += Math.pow(contribution - 1 / Network.size(), 2);
        }
        return potential;
    }
}
