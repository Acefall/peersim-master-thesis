package outputs;

import peersim.config.Configuration;
import peersim.core.Network;
import protocols.AggregationProtocol;

import java.io.IOException;

/**
 * Logs the mean of all inputs in every round.
 * */
public class GroundTruthLogger extends BufferedLogger{
    private static final String PAR_PROT = "protocol";
    private final int protocolID;

    public GroundTruthLogger(String name) {
        super(name);
        protocolID = Configuration.getPid(name + "." + PAR_PROT);
    }

    @Override
    protected void writeToFile() {
        double mean = 0.0;
        for (int i = 0; i < Network.size(); i++) {
            AggregationProtocol protocol = (AggregationProtocol) Network.get(i).getProtocol(protocolID);
            mean += protocol.getInput()/Network.size();
        }

        try{
            bufferedWriter.write(Double.toString(mean));
            bufferedWriter.newLine();
        }catch(IOException e){
            System.out.println("Could not write to file.");
        }

    }
}
