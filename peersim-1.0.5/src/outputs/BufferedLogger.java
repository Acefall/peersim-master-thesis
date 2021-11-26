package outputs;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;


/**
 * Provides easy writing to file using a buffer to reduce disc accesses.
 * */
public abstract class BufferedLogger implements Control {
    /**
     * Parameter that defines the directory in which the output file will be stored.
     */
    private static final String PAR_OUTPUT_DIR = "outputDir";
    /**
     * Parameter that defines the name of the output file.
     */
    private static final String PAR_LOG_FILE = "logFile";

    protected BufferedWriter bufferedWriter;

    public BufferedLogger(String name){
        String outputDir = Configuration.getString(PAR_OUTPUT_DIR);
        if(Configuration.contains("SIZE")){
            outputDir += "/"+Configuration.getInt("SIZE");
        }
        if(Configuration.contains("PARAMETER_1")){
            outputDir += "/"+Configuration.getString("PARAMETER_1");
        }
        if(Configuration.contains("PARAMETER_2")){
            outputDir += "/"+Configuration.getString("PARAMETER_2");
        }
        if(Configuration.contains("PARAMETER_3")){
            outputDir += "/"+Configuration.getString("PARAMETER_3");
        }
        if(Configuration.contains("PARAMETER_4")){
            outputDir += "/"+Configuration.getString("PARAMETER_4");
        }
        if(Configuration.contains(("PARAMETER_5"))){
            outputDir += "/"+Configuration.getString("PARAMETER_5");
        }
        if(Configuration.contains(("RUN"))){
            outputDir += "/"+Configuration.getString("RUN");
        }





        File dir = new File(outputDir);
        dir.mkdirs();

        String logfile = Configuration.getString(name + "." + PAR_LOG_FILE);

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
        if(CommonState.getPhase() == CommonState.POST_SIMULATION){
            try{
                bufferedWriter.close();
            } catch(IOException e){
                System.out.println("Could not close buffer.");
            }
        }else{
            writeToFile();
        }


        return false;
    }
}
