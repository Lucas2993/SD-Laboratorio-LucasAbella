package ar.edu.unp.madryn.livremarket.common.consistency;

import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SnapshotController {
    @Getter
    private String serverID;
    @Getter
    private List<String> otherServers;
    @Getter
    private boolean takingSnapshot;

    private MarkManager markManager;
    @Setter
    private ServerSnapshotManager serverSnapshotManager;
    private Gson gson;

    public SnapshotController(String serverID) {
        this.serverID = serverID;
        this.otherServers = new ArrayList<>();
        this.takingSnapshot = false;
        this.gson = new Gson();
    }

    public void init(String... skippedMarks){
        /* Iniciar el proceso guardando el estado y enviando la marca a los demas servidores */
        this.takingSnapshot = true;

        /* Reiniciar el administrador de marcas */
        this.markManager = new MarkManager(this.otherServers);

        Logging.info("Snapshot: Proceso iniciado!");

        /* Inicializar el administrador del snapshot y hacer que guarde los datos pendientes actuales */
        this.serverSnapshotManager.init();
        if(this.serverSnapshotManager.storeDataToSnapshot()){
            Logging.info("Snapshot: Estado actual guardado!");
        }

        if(this.markManager.sendMarksToServers()){
            Logging.info("Snapshot: Las marcas fueron enviadas a los demas servidores!");
        }

        /* Quitar marca a esperar del servidor que inicio el proceso en enviando una marca al propio */
        this.markManager.registerMark(skippedMarks);
    }

    public void handleMessage(String serverFromID, Map<String,String> messageData){
        String message = this.gson.toJson(messageData);

        this.handleMessage(serverFromID, message);
    }

    public void handleMessage(String serverFromID, String message){
        /* Si aun no se recibió la marca del servidor de origen se aloja */
        if(!this.takingSnapshot){
            return;
        }

        if(this.markManager.hasReceivedMark(serverFromID)){
            return;
        }

        Logging.info("Snapshot: Mensaje recibido y guardado! (Canal de origen = " + serverFromID + ")");
        this.serverSnapshotManager.storeMessage(serverFromID, message);
    }

    public void handleMark(String serverFromID){
        /* Se registra la marca recibida */
        if(!this.takingSnapshot){
            /* Se inicia el proceso a partir de una primer marca recibida cuando no se estaba realizando aun */
            Logging.info("Snapshot: Primera marca recibida, se inicia el proceso!");
            this.init(serverFromID);
            return;
        }

        if(this.markManager.registerMark(serverFromID)){
            Logging.info("Snapshot: Marca recibida y registrada! (Canal de origen = " + serverFromID + ")");
        }

        /* Si fue la ultima marca se finaliza el proceso y se persiste el resultado */
        if(this.markManager.allMarksReceived()){
            /* Proceso finalizado */
            if(this.serverSnapshotManager.storeSnapshot()){
                Logging.info("Snapshot: Snapshot guardado correctamente!");
            }
            this.takingSnapshot = false;
            Logging.info("Snapshot: La creacion ha finalizado!");
        }
    }

    public void addOtherServer(String serverID){
        this.otherServers.add(serverID);
    }
}
