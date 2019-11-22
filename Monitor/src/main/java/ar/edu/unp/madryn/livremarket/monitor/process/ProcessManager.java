package ar.edu.unp.madryn.livremarket.monitor.process;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import ar.edu.unp.madryn.livremarket.monitor.utils.LocalDefinitions;
import lombok.Setter;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            return false;
        }

        String serverPath = this.configuration.getValue(LocalDefinitions.FILE_CONFIGURATION_START_ID + serverID);
        if(StringUtils.isEmpty(serverPath)){
            return false;
        }

        String folder = this.configuration.getValue(LocalDefinitions.FOLDER_CONFIGURATION_ID);
        if(!StringUtils.isEmpty(folder)){
            serverPath = folder + serverPath;
        }

        String command = this.configuration.getValue(LocalDefinitions.COMMAND_CONFIGURATION_ID);
        if(StringUtils.isEmpty(command)){
            return false;
        }

        command = command.replace(LocalDefinitions.SERVER_ID_COMMAND_REPLACE, serverPath);

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(command.split(" "));
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();

            this.processes.put(serverID, process);
            return true;
        }
        catch (IOException e){
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

    public List<String> getLogs(String serverID){
        List<String> result = new ArrayList<>();
        if(!this.processes.containsKey(serverID)){
            return result;
        }

        Process process = this.processes.get(serverID);

        try {
            InputStream inputStream = process.getInputStream();

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = "";
            char inputChar;
            while(bufferedReader.ready()){
                inputChar = (char)bufferedReader.read();
                if(CharUtils.isAsciiControl(inputChar)){
                    result.add(line);
                    line = "";
                    continue;
                }

                line += inputChar;
            }
        }
        catch (IOException e){
            Logging.error("No se pudo leer la entrada del proceso! (Servidor = " + serverID + ")");
        }

        return result;
    }
}
