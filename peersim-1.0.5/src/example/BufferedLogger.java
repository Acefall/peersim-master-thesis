package example;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

public abstract class BufferedLogger implements Control {
    private static final String PAR_OUTPUT_DIR = "outputDir";
    private String outputDir;

    private static final String PAR_LOG_FILE = "logFile";
    private final String logfile;

    protected BufferedWriter bufferedWriter;

    public BufferedLogger(String name){
        outputDir = Configuration.getString(PAR_OUTPUT_DIR);
        if(Configuration.contains("SIZE")){
            outputDir += "/"+Configuration.getInt("SIZE");
        }
        if(Configuration.contains("KEEP_RATIO")){
            outputDir += "/"+Configuration.getString("KEEP_RATIO");
        }
        if(Configuration.contains(("RUN"))){
            outputDir += "/"+Configuration.getInt("RUN");
        }

        File dir = new File(outputDir);
        dir.mkdirs();

        logfile = Configuration.getString(name + "." + PAR_LOG_FILE);

        Path path = Paths.get(outputDir + "/" + logfile);
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException ex) {
            System.err.println("File already exists. Truncating.");
            try {
                Files.write(path, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                System.err.println("Could not truncate the file");
            }
        } catch (IOException e) {
            System.err.println("Could not create the file.");
        }


        try {
            File logFile = new File(outputDir + "/" + logfile);
            FileWriter logFileWriter = new FileWriter(logFile);
            bufferedWriter = new BufferedWriter(logFileWriter);
        } catch(IOException e){
            System.out.println("Could not open the file.");
        }
    }

    protected void writeToFile() {}

    @Override
    public boolean execute() {
        writeToFile();

        if(CommonState.getPhase() == CommonState.POST_SIMULATION){
            try{
                bufferedWriter.close();
            } catch(IOException e){
                System.out.println("Could not close buffer.");
            }
        }
        return false;
    }
}
