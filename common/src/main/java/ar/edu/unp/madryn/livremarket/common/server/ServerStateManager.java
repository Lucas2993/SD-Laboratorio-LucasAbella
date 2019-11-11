package ar.edu.unp.madryn.livremarket.common.server;

import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

public class ServerStateManager {

    private Collection<ServerState> serverStates;
    @Setter
    private String stateCollectionName;
    @Setter
    private String idField;

    @Setter
    private DataProvider dataProvider;

    private static ServerStateManager instance;

    public static ServerStateManager getInstance() {
        if(instance == null){
            instance = new ServerStateManager();
        }

        return instance;
    }

    private ServerStateManager() {
        this.serverStates = new ArrayList<>();
    }

    /* Manejo local de estados */

    public boolean addServerState(ServerState serverState){
        if(this.serverStates.contains(serverState)){
            return false;
        }

        this.serverStates.add(serverState);
        return true;
    }

    public ServerState getServerStateByID(String id){
        return this.serverStates.stream()
                .filter(serverState -> serverState.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /* Persistencia */

    public boolean storeStates(){
        /* Guardar estado */
        this.serverStates.stream()
                .filter(ServerState::isModified)
                .forEach(serverState -> {
                    serverState.setModified(false);
                    if(!serverState.isPersisted()){
                        serverState.setPersisted(true);
                        dataProvider.insertElement(serverState, stateCollectionName);
                        return;
                    }

                    dataProvider.updateElement(idField, serverState.getId(), serverState, stateCollectionName);
                });

        return true;
    }

    public boolean retrieveStates(){
        /* Recuperar registro de la base de datos. */
        this.serverStates = this.dataProvider.getCollection(this.stateCollectionName, ServerState.class);

        return true;
    }

}
