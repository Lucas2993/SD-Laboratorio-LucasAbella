package ar.edu.unp.madryn.livremarket.monitor.process;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ProcessData {
    @Getter
    private Process process;
    @Getter
    private List<String> logs;

    public ProcessData(Process process) {
        this.process = process;
        this.logs = new ArrayList<>();
    }

    public void addLog(String log){
        this.logs.add(log);
    }
}
