package example;

import approximation.Approximation;
import peersim.config.Configuration;
import peersim.core.Network;

import java.io.IOException;

public class InputLogger extends BufferedLogger{
    private static final String PAR_PROT = "protocol";
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
                bufferedWriter.newLine();
            }catch(IOException e){
                System.out.println("Could not write to file.");
            }
        }
    }
}
