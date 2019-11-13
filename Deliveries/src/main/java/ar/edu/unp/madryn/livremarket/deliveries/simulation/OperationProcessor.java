package ar.edu.unp.madryn.livremarket.deliveries.simulation;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.server.ServerState;
import ar.edu.unp.madryn.livremarket.common.server.ServerStateManager;
import ar.edu.unp.madryn.livremarket.common.simulation.MessageProcessor;
import ar.edu.unp.madryn.livremarket.common.simulation.PendingOperation;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import ar.edu.unp.madryn.livremarket.deliveries.utils.LocalDefinitions;
import lombok.Setter;

import java.util.Map;

public class OperationProcessor extends MessageProcessor {
    @Setter
    private ServerStateManager serverStateManager;

    @Override
    public PendingOperation processGeneralMessage(String operation, Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        /* Recuperar registro de la base de datos. */
        ServerState serverState = this.serverStateManager.getServerStateByID(purchaseID);

        if(serverState == null){
            serverState = new ServerState(purchaseID);
            serverState.saveData(MessageCommonFields.PURCHASE_ID, purchaseID);
        }

        switch (operation) {
            case Operations.GET_DELIVERY_COST_OPERATION:
                Logging.info("Solicitud de costo de envio recibida! (ID =" + purchaseID + ")");

                if(data.containsKey(LocalDefinitions.REQUESTED_COST_FIELD)){
                    Logging.error("Error: Operacion duplicada! (ID =" + purchaseID + ")");
                }

                serverState.saveData(LocalDefinitions.REQUESTED_COST_FIELD, String.valueOf(true));

                break;
            case Operations.BOOK_SHIPMENT_OPERATION:
                Logging.info("Solicitud para agendar el envio recibida! (ID =" + purchaseID + ")");

                if(data.containsKey(LocalDefinitions.REQUESTED_DELIVERY_BOOK_FIELD)){
                    Logging.error("Error: Operacion duplicada! (ID =" + purchaseID + ")");
                }

                serverState.saveData(LocalDefinitions.REQUESTED_DELIVERY_BOOK_FIELD, String.valueOf(true));

                break;
            default:
                Logging.error("Error: Operacion '" + operation + "' no reconocida!");
        }

        PendingOperation pendingOperation = new PendingOperation();

        pendingOperation.setData(serverState.getData());

        return pendingOperation;
    }

    @Override
    public PendingOperation processResultMessage(String id, Map<String, String> data) {
        return null;
    }
}
