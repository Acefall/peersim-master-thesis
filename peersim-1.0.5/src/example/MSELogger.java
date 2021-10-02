package example;

import approximation.Approximation;
import peersim.config.Configuration;
import peersim.core.Network;

import java.io.IOException;

public class MSELogger extends BufferedLogger{
    private static final String PAR_PROT = "protocol";
    private final int protocolID;

    public MSELogger(String name) {
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
        double mse = 0.0;
        for (int i = 0; i < Network.size(); i++) {
            Approximation protocol = (Approximation) Network.get(i).getProtocol(protocolID);
            mse += Math.pow(protocol.getApproximation() - mean, 2)/Network.size();
        }

        try{
            bufferedWriter.write(String.valueOf(mse));
            bufferedWriter.newLine();
        }catch(IOException e){
            System.out.println("Could not write to file.");
        }

    }
}
