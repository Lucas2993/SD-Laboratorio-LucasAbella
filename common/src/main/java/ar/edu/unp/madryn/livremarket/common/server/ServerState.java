package ar.edu.unp.madryn.livremarket.common.server;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ServerState {
    @Getter@Setter
    private String id;
    @Getter@Setter
    private Map<String,String> data;
    @Getter@Setter
    private boolean modified;
    @Getter@Setter
    private boolean persisted;

    public ServerState(String id) {
        this.id = id;
        this.data = new ConcurrentHashMap<>();
        this.modified = true;
        this.persisted = false;
    }

    public void saveData(String key, String value){
        this.modified = true;
        this.data.put(key, value);
    }

    public void saveData(Map<String,String> data){
        this.modified = true;
        this.data.putAll(data);
    }

    public String getSingleData(String key){
        return this.data.get(key);
    }

    public boolean containsData(String key){
        return this.data.containsKey(key);
    }

    /* Equals */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerState that = (ServerState) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
