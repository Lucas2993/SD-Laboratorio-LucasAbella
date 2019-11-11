package ar.edu.unp.madryn.livremarket.common.threads;

import ar.edu.unp.madryn.livremarket.common.server.ServerStateManager;
import lombok.Setter;

public class StatePersistenceWorker extends Thread {
    @Setter
    private ServerStateManager serverStateManager;

    @Override
    public void run() {
        System.out.println("Guardando los estados!");
        if(this.serverStateManager.storeStates()){
            System.out.println("Estados guardados correctamente!");
            return;
        }

        System.err.println("Error: Los estados no han podido guardarse!");
    }
}
