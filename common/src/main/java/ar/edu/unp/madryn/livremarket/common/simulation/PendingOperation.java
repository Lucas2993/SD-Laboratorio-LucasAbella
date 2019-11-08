package ar.edu.unp.madryn.livremarket.common.simulation;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class PendingOperation {
    @Getter
    @Setter
    private Map<String,String> data;

    public PendingOperation() {
        this.data = new HashMap<>();
    }

    public void addData(Map<String,String> dataToAdd){
        this.data.putAll(dataToAdd);
    }

    public void addData(String key, String value){
        this.data.put(key, value);
    }
}
