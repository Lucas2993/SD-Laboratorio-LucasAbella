package ar.edu.unp.madryn.livremarket.common.consistency;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MarkManager {
    @Getter
    private List<String> notReceived;
    @Setter
    private static CommunicationHandler communicationHandler;

    public MarkManager(List<String> servers) {
        this.notReceived = new ArrayList<>(servers);
    }

    public boolean registerMark(String... serverIDs){
        if(ArrayUtils.isEmpty(serverIDs)){
            return false;
        }

        boolean result = true;
        for(String serverID : serverIDs){
            if(!this.notReceived.remove(serverID)){
                result = false;
            }
        }

        return result;
    }

    public boolean allMarksReceived(){
        return this.notReceived.isEmpty();
    }

    public boolean hasReceivedMark(String serverID){
        return !this.notReceived.contains(serverID);
    }

    public boolean sendMarksToServers(){
        if(communicationHandler == null){
            return false;
        }

        for(String serverName : this.notReceived){
            communicationHandler.sendMessage(MessageType.MARK, serverName, new HashMap<>());
        }

        return true;
    }
}
