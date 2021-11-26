package protocols.pullSum;

import outputs.BufferedLogger;
import protocols.HasContributions;
import peersim.config.Configuration;
import peersim.core.Network;

import java.io.IOException;

/**
 * Logs the potential of every node individually in every round.
 * The potential function used is defined by Kempe et al.
 * */

public class PotentialLogger extends BufferedLogger {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";
    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int protocolID;

    public PotentialLogger(String name) {
        super(name);
        protocolID = Configuration.getPid(name + "." + PAR_PROT);
    }

    protected void writeToFile() {
        try {
            for (int i = 0; i < Network.size(); i++) {
                HasContributions protocol = (HasContributions) Network.get(i).getProtocol(protocolID);
                bufferedWriter.write(String.valueOf(potential(protocol.getContributions())));
                bufferedWriter.write(",");
            }
            bufferedWriter.newLine();
        }catch(IOException e){
            System.out.println("Could not write to file.");
        }
    }

    private double weight(Double[] contributions){
        double weight = 0;
        for (Double contribution : contributions) {
            weight += contribution;
        }
        return  weight;
    }

    private double potential(Double[] contributions){
        double weight = weight(contributions);
        double potential = 0;
        for (Double contribution : contributions) {
            potential += Math.pow(contribution - weight / Network.size(), 2);
        }
        return potential;
    }
}
