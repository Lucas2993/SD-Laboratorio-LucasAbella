package ar.edu.unp.madryn.livremarket.common.consistency;

import ar.edu.unp.madryn.livremarket.common.consistency.models.ServerSnapshot;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.server.ServerState;
import ar.edu.unp.madryn.livremarket.common.server.ServerStateManager;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;

public class ServerSnapshotManager {
    @Getter
    private ServerSnapshot serverSnapshot;
    @Setter
    private ServerStateManager serverStateManager;
    @Setter
    private static DataProvider dataProvider;

    public void init(){
        this.serverSnapshot = new ServerSnapshot();
    }

    public void storeMessage(String serverID, String message){
        this.serverSnapshot.addMessage(serverID, message);
    }

    public boolean storeDataToSnapshot(){
        Collection<ServerState> serverStates = this.serverStateManager.getServerStatesNotPersisted();
        if(CollectionUtils.isEmpty(serverStates)){
            return false;
        }

        this.serverSnapshot.addServerStates(serverStates);
        return true;
    }

    public boolean storeSnapshot(){
        if(dataProvider == null){
            return false;
        }

        dataProvider.insertElement(this.serverSnapshot, Definitions.SNAPSHOTS_COLLECTION_NAME);
        return true;
    }
}
