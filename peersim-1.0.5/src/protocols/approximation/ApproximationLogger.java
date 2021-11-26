package protocols.approximation;

import outputs.BufferedLogger;
import peersim.config.Configuration;
import peersim.core.Network;

import java.io.IOException;

/**
 * Logs the approximation fo every nod ein ever round.
 * */
public class ApproximationLogger extends BufferedLogger {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";

    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int protocolID;

    public ApproximationLogger(String name) {
        super(name);
        protocolID = Configuration.getPid(name + "." + PAR_PROT);
    }

    @Override
    protected void writeToFile() {
        for (int i = 0; i < Network.size(); i++) {
            Approximation protocol = (Approximation) Network.get(i).getProtocol(protocolID);
            double approximation = protocol.getApproximation();
            try{
                bufferedWriter.write(String.valueOf(approximation));
                bufferedWriter.newLine();
            }catch(IOException e){
                System.out.println("Could not write to file.");
            }
        }
    }
}
