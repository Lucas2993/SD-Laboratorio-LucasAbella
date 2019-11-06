package ar.edu.unp.madryn.livremarket.products.messages;

import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import ar.edu.unp.madryn.livremarket.common.simulation.SimulationController;
import lombok.Setter;

import java.util.Map;

public class ControlMessage implements MessageHandler {
    @Setter
    private SimulationController simulationController;

    public ControlMessage() {
    }

    @Override
    public void handle(Map<String, String> data) {
        System.out.println("Paso recibido!");
        if(this.simulationController.step()){
            System.out.println("Paso realizado con exito!");
        }
        else {
            System.err.println("En algo le erramo'!");
        }
    }
}
