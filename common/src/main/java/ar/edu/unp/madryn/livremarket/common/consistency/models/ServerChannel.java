package ar.edu.unp.madryn.livremarket.common.consistency.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerChannel {
    @Getter
    private String serverID;
    @Getter
    private List<String> messages;

    public ServerChannel(String serverID) {
        this.serverID = serverID;
        this.messages = new ArrayList<>();
    }

    public void addMessage(String message){
        this.messages.add(message);
    }

    public boolean isEmpty(){
        return this.messages.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerChannel that = (ServerChannel) o;
        return Objects.equals(serverID, that.serverID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverID);
    }
}
