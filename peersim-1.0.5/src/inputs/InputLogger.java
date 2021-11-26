package inputs;

import protocols.AggregationProtocol;
import outputs.BufferedLogger;
import peersim.config.Configuration;
import peersim.core.Network;

import java.io.IOException;

/**
 * Every round writes the inputs of all nodes to a csv file.
 * */
public class InputLogger extends BufferedLogger {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";
    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int protocolID;

    public InputLogger(String name) {
        super(name);
        protocolID = Configuration.getPid(name + "." + PAR_PROT);
    }

    @Override
    protected void writeToFile() {
        for (int i = 0; i < Network.size(); i++) {
            AggregationProtocol protocol = (AggregationProtocol) Network.get(i).getProtocol(protocolID);
            double input = protocol.getInput();
            try{
                bufferedWriter.write(String.valueOf(input));
                bufferedWriter.write(",");
            }catch(IOException e){
                System.out.println("Could not write to file.");
            }
        }
        try{
            bufferedWriter.newLine();
        }catch(IOException e){
            System.out.println("Could not write to file.");
        }

    }
}
