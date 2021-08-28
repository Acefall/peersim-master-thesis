package example.minimize;

import org.nfunk.jep.function.Str;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalStats;
import peersim.vector.SingleValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MinObserver implements Control {
    /**
     * The protocol to operate on.
     *
     * @config
     */
    private static final String PAR_PROT = "protocol";

    private static final String PAR_LOGFILE = "logfile";

    /**
     * The name of this observer in the configuration file. Initialized by the
     * constructor parameter.
     */
    private final String name;

    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int pid;

    /**
     * This object keeps track of the values injected and produces statistics.
     * More details in: {@link peersim.util.IncrementalStats} .
     */
    private IncrementalStats stats = null;

    private String logFile;


    public MinObserver(String name){
        this.name = name;
        pid = Configuration.getPid(name+"."+PAR_PROT);
        stats = new IncrementalStats();
    }

    public boolean execute(){
        final int len = Network.size();
        for(int i = 0; i < len; i++){
            SingleValue protocol = (SingleValue) Network.get(i).getProtocol(pid);
            double value = protocol.getValue();
            stats.add(value);
        }

        double proportionTrueValue = ((float) stats.getMinCount())/Network.size();

        stats.reset();

        if (proportionTrueValue >= 1) {
            System.out.println(Network.size() + "," + CommonState.getTime());
            try {
                Files.write(Paths.get("Z:\\projects\\Universität\\ss21\\Project\\stats\\push.csv"),
                        (Network.size() + "," + CommonState.getTime() + "\n").getBytes(), StandardOpenOption.APPEND);
            }catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
            return true;
        }

        return false;
    }
}
