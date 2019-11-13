package ar.edu.unp.madryn.livremarket.payments.simulation;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.server.ServerState;
import ar.edu.unp.madryn.livremarket.common.server.ServerStateManager;
import ar.edu.unp.madryn.livremarket.common.simulation.MessageProcessor;
import ar.edu.unp.madryn.livremarket.common.simulation.PendingOperation;
import ar.edu.unp.madryn.livremarket.payments.utils.LocalDefinitions;
import lombok.Setter;

import java.util.Map;

public class OperationProcessor extends MessageProcessor {
    @Setter
    private ServerStateManager serverStateManager;

    public OperationProcessor() {
    }

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
            case Operations.AUTHORIZE_PAYMENT_OPERATION:
                System.out.println("Solicitud de autorizacion de pago recibida! (ID =" + purchaseID + ")");

                if(data.containsKey(LocalDefinitions.REQUESTED_PAYMENT_FIELD)){
                    System.err.println("Error: Operacion duplicada! (ID =" + purchaseID + ")");
                }

                serverState.saveData(LocalDefinitions.REQUESTED_PAYMENT_FIELD, String.valueOf(true));

                break;
            default:
                System.err.println("Error: Operacion '" + operation + "' no reconocida!");
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
