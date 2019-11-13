package ar.edu.unp.madryn.livremarket.common.messages.types;

import ar.edu.unp.madryn.livremarket.common.messages.Controls;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import ar.edu.unp.madryn.livremarket.common.server.ServerStateManager;
import ar.edu.unp.madryn.livremarket.common.simulation.SimulationController;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class ControlMessage implements MessageHandler {
    @Setter
    private SimulationController simulationController;
    @Setter
    private ServerStateManager serverStateManager;

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
                    if(this.simulationController.step()){
                        Logging.info("Paso realizado con exito!");
                    }
                    else {
                        Logging.error("No hay pasos para realizar!");
                    }
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
            default:
                Logging.error("Error: Operacion '" + operation + "' no reconocida!");
        }
    }
}
