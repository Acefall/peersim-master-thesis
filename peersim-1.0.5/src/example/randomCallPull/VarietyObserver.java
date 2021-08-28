package example.randomCallPull;

import messagePassing.MPProtocol;
import messagePassing.Message;
import messagePassing.randomCallModel.PullCall;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class VarietyObserver implements Control {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";
    /**
     * Parameter that defines the output directory.
     */
    private static final String PAR_OUTPUT_DIR = "outputDir";


    /**
     * Protocol identifier, obtained from config property {@link #PAR_PROT}.
     */
    private final int protocolID;

    /**
     * The name of the output file for the varieties.
     */
    private final String varietyLogfile = "/varieties.csv";

    /**
     * The name of the output file for the maximum scaling factors.
     */
    private final String maxScalingFactorLogfile = "/maximumScalingFactor.csv";

    /**
     * The directory in which the output file is saved, obtained from {@link #PAR_OUTPUT_DIR}.
     */
    private final String outputDir;


    /**
     * Maps VxV->N. Stores the variety as defined in the paper.
     */
    HashMap<Node, HashMap<Node, Long>> varieties;
    HashMap<Node, HashMap<Node, Long>> buffer;


    /**
     * Initializes the varieties and truncates the output files.
     *
     * @param name As named in the configuration file
     */
    public VarietyObserver(String name) {
        protocolID = Configuration.getPid(name + "." + PAR_PROT);
        outputDir = Configuration.getString(PAR_OUTPUT_DIR);
        File dir = new File(outputDir);
        dir.mkdirs();


        buffer = new HashMap<>();
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            buffer.put(node, new HashMap<>());
            buffer.get(node).put(node, 1L);
        }
        commit();


        Path pathVarieties = Paths.get(outputDir + varietyLogfile);
        Path pathMaxScalings = Paths.get(outputDir + maxScalingFactorLogfile);
        try {
            Files.createFile(pathVarieties);
            Files.createFile(pathMaxScalings);
        } catch (FileAlreadyExistsException ex) {
            System.err.println("File already exists. Truncating.");
            try {
                Files.write(pathVarieties, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
                Files.write(pathMaxScalings, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                System.err.println("Could not truncate the file");
            }
        } catch (IOException e) {
            System.err.println("Could not create the file.");
        }


    }

    /**
     * Update the varieties based on this message.
     *
     * @param m
     */
    public void countMessage(Message m) {
        for (var entry : varieties.get(m.getSender()).entrySet()) {
            incrementCommunication(m.getReceiver(), entry.getKey(), entry.getValue());
        }
    }

    /**
     * Increment the number of times the sender value is used in the aggregation of the receiver value.
     *
     * @param receiver Receiver of the message
     * @param sender   Sender of the message
     * @param count    How often the value of sender has been used
     */
    private void incrementCommunication(Node receiver, Node sender, Long count) {
        if (!buffer.containsKey(receiver)) {
            buffer.put(receiver, new HashMap<>());
        }
        HashMap<Node, Long> variety = buffer.get(receiver);
        if (!variety.containsKey(sender)) {
            variety.put(sender, 0L);
        }
        variety.put(sender, variety.get(sender) + count);
    }

    /**
     * Copy buffer to varieties
     */
    public void commit() {
        varieties = new HashMap<>();
        for (Map.Entry<Node, HashMap<Node, Long>> entry : buffer.entrySet()) {
            varieties.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
    }

    /**
     * Process all inbound messages of all nodes. Log varieties and maximum scaling factors to file.
     *
     * @return false
     */
    @Override
    public boolean execute() {
        for (int i = 0; i < Network.size(); i++) {
            MPProtocol protocol = (MPProtocol) Network.get(i).getProtocol(protocolID);
            var messages = protocol.getInBoundMessages();
            while (messages.hasNext()) {
                Message m = messages.next();
                if (!(m instanceof PullCall)) {
                    countMessage(m);
                }
            }
        }
        commit();
        try {
            Files.write(Paths.get(outputDir + varietyLogfile),
                    (this.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
            Files.write(Paths.get(outputDir + maxScalingFactorLogfile),
                    (maxScalingFactorToString() + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Could not write to file.");
        }
        return false;
    }

    /**
     * Build a string of maximum scaling factors of all nodes.
     *
     * @return the comma separated maximum scaling factors
     */
    public String maxScalingFactorToString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            if (!varieties.containsKey(node)) {
                result.append(0);
            } else {
                result.append(Collections.max(varieties.get(node).values()));
            }
            result.append(",");
        }
        return result.toString();
    }

    /**
     * Build a string of the varieties of all nodes.
     *
     * @return the comma separated varieties
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            if (!varieties.containsKey(node)) {
                result.append(0);
            } else {
                result.append(varieties.get(node).size());
            }
            result.append(",");
        }
        return result.toString();
    }

}
