package protocols.randomCallPull;

import messagePassing.MPProtocol;
import messagePassing.Message;
import messagePassing.randomCallModel.PullCall;
import outputs.BufferedLogger;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;


import java.io.IOException;
import java.util.*;

public class VarietyLogger extends BufferedLogger implements Control {
    /**
     * Parameter that defines the protocol to operate on.
     */
    private static final String PAR_PROT = "protocol";

    /**
     * Protocol identifier, obtained from config property {@link #PAR_PROT}.
     */
    private final int protocolID;


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
    public VarietyLogger(String name) {
        super(name);
        protocolID = Configuration.getPid(name + "." + PAR_PROT);

        buffer = new HashMap<>();
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            buffer.put(node, new HashMap<>());
            buffer.get(node).put(node, 1L);
        }
        commit();
    }

    /**
     * Update the varieties based on this message.
     *
     * @param m The message that is to be counted
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
        writeToFile();
        return false;
    }

    public void writeMaxScalingFactors() {
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            try{
                if (!varieties.containsKey(node)) {
                    bufferedWriter.write(String.valueOf(0));
                } else {
                    bufferedWriter.write(String.valueOf(Collections.max(varieties.get(node).values())));
                }
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


    public void writeVarieties() {
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            try{
                if (!varieties.containsKey(node)) {
                    bufferedWriter.write(String.valueOf(0));
                } else {
                    bufferedWriter.write(String.valueOf(varieties.get(node).size()));
                }
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

    @Override
    protected void writeToFile() {
        writeVarieties();
        writeMaxScalingFactors();
    }
}
