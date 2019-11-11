package ar.edu.unp.madryn.livremarket.common.threads;

import ar.edu.unp.madryn.livremarket.common.server.ServerStateManager;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import lombok.Setter;

public class StatePersistenceWorker extends Thread {
    @Setter
    private ServerStateManager serverStateManager;

    @Override
    public void run() {
        Logging.info("Guardando los estados!");
        if(this.serverStateManager.storeStates()){
            Logging.info("Estados guardados correctamente!");
            return;
        }

        Logging.error("Error: Los estados no han podido guardarse!");
    }
}
