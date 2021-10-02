package example;

import approximation.Approximation;
import peersim.config.Configuration;
import peersim.core.Network;

import java.io.IOException;

public class ApproximationLogger extends BufferedLogger{
    private static final String PAR_PROT = "protocol";
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
