package ar.edu.unp.madryn.livremarket.common.messages.types;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.consistency.SnapshotController;
import ar.edu.unp.madryn.livremarket.common.messages.Controls;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.server.ServerStateManager;
import ar.edu.unp.madryn.livremarket.common.simulation.SimulationController;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ControlMessage implements MessageHandler {
    @Setter
    private SimulationController simulationController;
    @Setter
    private ServerStateManager serverStateManager;
    @Setter
    private SnapshotController snapshotController;
    @Setter
    private CommunicationHandler communicationHandler;

    public ControlMessage() {
    }

    @Override
    public void handle(Map<String, String> data) {
        Logging.info("Mensaje de control recibido!");

        String operation = data.get(Definitions.CONTROL_REFERENCE_KEY);

        if(StringUtils.isEmpty(operation)){
            return;
        }

        switch (operation){
            case Controls.MAKE_STEP:
                if(!simulationController.isAutomatic()){
                    Map<String,String> resultData = new HashMap<>();
                    if(this.simulationController.step()){
                        Logging.info("Paso realizado con exito!");
                        resultData.put(MessageCommonFields.RESULT_MESSAGE, "Paso realizado con exito!");
                    }
                    else {
                        Logging.error("No hay pasos para realizar!");
                        resultData.put(MessageCommonFields.RESULT_MESSAGE, "No hay pasos para realizar!");
                    }
                    communicationHandler.sendMessage(MessageType.RESULT, Definitions.MONITOR_SERVER_NAME, resultData);
                }
                break;
            case Controls.DO_PERSIST:
                if(this.serverStateManager.storeStates()){
                    Logging.info("Estado guardado exitosamente!");
                }
                else {
                    Logging.error("Error: El estado no pudo ser guardado!");
                }
                break;
            case Controls.BEGIN_SNAPSHOT:
                this.snapshotController.init();
                break;
            default:
                Logging.error("Error: Operacion '" + operation + "' no reconocida!");
        }
    }
}
