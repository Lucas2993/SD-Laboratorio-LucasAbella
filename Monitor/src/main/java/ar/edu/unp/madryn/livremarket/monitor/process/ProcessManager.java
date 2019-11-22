package ar.edu.unp.madryn.livremarket.monitor.process;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import ar.edu.unp.madryn.livremarket.monitor.utils.LocalDefinitions;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProcessManager {
    private Map<String, Process> processes;

    @Setter
    private ConfigurationSection configuration;

    private static ProcessManager instance;

    public static ProcessManager getInstance() {
        if(instance == null){
            instance = new ProcessManager();
        }
        return instance;
    }

    private ProcessManager() {
        this.processes = new HashMap<>();
    }

    public boolean initServer(String serverID){
        if(StringUtils.isEmpty(serverID)){
            Logging.info("Salio por aca!");
            return false;
        }

        String serverPath = this.configuration.getValue(LocalDefinitions.FILE_CONFIGURATION_START_ID + serverID);
        if(StringUtils.isEmpty(serverPath)){
            Logging.info("Salio por aca1!");
            return false;
        }

        String folder = this.configuration.getValue(LocalDefinitions.FOLDER_CONFIGURATION_ID);
        if(!StringUtils.isEmpty(folder)){
            serverPath = folder + serverPath;
        }

        String command = this.configuration.getValue(LocalDefinitions.COMMAND_CONFIGURATION_ID);
        if(StringUtils.isEmpty(command)){
            Logging.info("Salio por aca2!");
            return false;
        }

        command = command.replace(LocalDefinitions.SERVER_ID_COMMAND_REPLACE, serverPath);

        Logging.info("Control:", command);

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(command.split(" "));

        try {
            Process process = processBuilder.start();

            this.processes.put(serverID, process);
            return true;
        }
        catch (IOException e){
            Logging.info("Salio por aca3!", e.getMessage());
            return false;
        }
    }

    public boolean killServer(String serverID){
        if(!this.processes.containsKey(serverID)){
            return false;
        }

        Process process = this.processes.remove(serverID);

        process.destroy();
        return true;
    }

    public boolean isRunning(String serverID){
        if(!this.processes.containsKey(serverID)){
            return false;
        }

        Process process = this.processes.get(serverID);

        return process.isAlive();
    }
}
