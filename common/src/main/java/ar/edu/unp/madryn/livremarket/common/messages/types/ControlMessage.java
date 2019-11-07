package ar.edu.unp.madryn.livremarket.common.messages.types;

import ar.edu.unp.madryn.livremarket.common.messages.Controls;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import ar.edu.unp.madryn.livremarket.common.simulation.SimulationController;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class ControlMessage implements MessageHandler {
    @Setter
    private SimulationController simulationController;

    public ControlMessage() {
    }

    @Override
    public void handle(Map<String, String> data) {
        System.out.println("Mensaje de control recibido!");

        String operation = data.get(Definitions.CONTROL_REFERENCE_KEY);

        if(StringUtils.isEmpty(operation)){
            return;
        }

        switch (operation){
            case Controls.MAKE_STEP:
                if(!simulationController.isAutomatic()){
                    if(this.simulationController.step()){
                        System.out.println("Paso realizado con exito!");
                    }
                    else {
                        System.err.println("No hay pasos para realizar!");
                    }
                }
                break;
            default:
                System.err.println("Error: Operacion '" + operation + "' no reconocida!");
        }
    }
}
