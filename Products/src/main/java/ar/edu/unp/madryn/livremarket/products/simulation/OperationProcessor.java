package ar.edu.unp.madryn.livremarket.products.simulation;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.messages.Results;
import ar.edu.unp.madryn.livremarket.common.server.ServerState;
import ar.edu.unp.madryn.livremarket.common.server.ServerStateManager;
import ar.edu.unp.madryn.livremarket.common.simulation.MessageProcessor;
import ar.edu.unp.madryn.livremarket.common.simulation.PendingOperation;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import ar.edu.unp.madryn.livremarket.products.utils.LocalDefinitions;
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

        /* Actualizar estado interno. */
        switch (operation) {
            case Operations.PRODUCT_RESERVATION_OPERATION:
                if(serverState.containsData(MessageCommonFields.PRODUCT_ID)) {
                    Logging.error("Error: Operacion duplicada!");
                    return null;
                }

                String productID = data.get(MessageCommonFields.PRODUCT_ID);
                serverState.saveData(MessageCommonFields.PRODUCT_ID, productID);
                serverState.saveData(LocalDefinitions.PRODUCT_RESERVATION_REQUESTED_FIELD, String.valueOf(true));
                break;
            case Operations.BOOK_SHIPMENT_OPERATION:
                if(serverState.containsData(MessageCommonFields.BOOKED_SHIPPING)) {
                    Logging.error("Error: Operacion duplicada!");
                    return null;
                }

                // TODO Registrar envio en la base de datos
                serverState.saveData(MessageCommonFields.BOOKED_SHIPPING, String.valueOf(true));
                break;
            default:
                Logging.error("Error: Operacion '" + operation + "' no reconocida!");
                return null;
        }

        PendingOperation pendingOperation = new PendingOperation();

        pendingOperation.setData(serverState.getData());

        return pendingOperation;
    }

    @Override
    public PendingOperation processResultMessage(String id, Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        /* Recuperar registro de la base de datos. */
        ServerState serverState = this.serverStateManager.getServerStateByID(purchaseID);

        if(serverState == null){
            serverState = new ServerState(purchaseID);
        }

        /* Actualizar estado interno. */
        switch (id) {
            case Results.INFRACTIONS_REFERENCE_ID:
                if(serverState.containsData(MessageCommonFields.HAS_INFRACTIONS)) {
                    Logging.error("Error: Informacion duplicada!");
                    return null;
                }
                String infractionsResult = data.get(MessageCommonFields.HAS_INFRACTIONS);

                serverState.saveData(MessageCommonFields.HAS_INFRACTIONS, infractionsResult);
                break;
            case Results.PAYMENT_AUTHORIZATION_REFERENCE_ID:
                if(serverState.containsData(MessageCommonFields.AUTHORIZED_PAYMENT)) {
                    Logging.error("Error: Informacion duplicada!");
                    return null;
                }
                String paymentResult = data.get(MessageCommonFields.AUTHORIZED_PAYMENT);

                serverState.saveData(MessageCommonFields.AUTHORIZED_PAYMENT, paymentResult);
                break;
            default:
                Logging.error("Error: ID de informacion '" + id + "' no reconocido!");
                return null;
        }

        PendingOperation pendingOperation = new PendingOperation();

        pendingOperation.setData(serverState.getData());

        return pendingOperation;
    }
}
