package ar.edu.unp.madryn.livremarket.common.consistency.models;

import ar.edu.unp.madryn.livremarket.common.server.ServerState;
import lombok.Getter;

import java.util.*;

public class ServerSnapshot {
    @Getter
    private Collection<ServerState> serverStates;
    @Getter
    private List<ServerChannel> serverChannels;

    public ServerSnapshot() {
        this.serverStates = new ArrayList<>();
        this.serverChannels = new ArrayList<>();
    }

    public ServerChannel getServerChannelByID(String serverID){
        Optional<ServerChannel> result = this.serverChannels.stream()
                .filter(serverChannel -> serverChannel.getServerID().equals(serverID))
                .findFirst();

        if(result.isPresent()){
            return result.get();
        }

        ServerChannel serverChannel = new ServerChannel(serverID);
        this.addServerChannel(serverChannel);

        return serverChannel;
    }

    public boolean addServerChannel(ServerChannel serverChannel){
        if(this.serverChannels.contains(serverChannel)){
            return false;
        }

        this.serverChannels.add(serverChannel);
        return true;
    }

    public boolean addServerChannel(String serverID){
        return this.addServerChannel(new ServerChannel(serverID));
    }

    public void addMessage(String serverID, String message){
        ServerChannel serverChannel = this.getServerChannelByID(serverID);

        serverChannel.addMessage(message);
    }

    public void addServerStates(Collection<ServerState> serverStates){
        this.serverStates.addAll(serverStates);
    }
}
