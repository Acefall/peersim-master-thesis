package example.pullSum;

import example.BufferedLogger;
import peersim.config.Configuration;
import peersim.core.Network;

import java.io.IOException;

public class WeightLogger extends BufferedLogger {
    private static final String PAR_PROT = "protocol";
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
