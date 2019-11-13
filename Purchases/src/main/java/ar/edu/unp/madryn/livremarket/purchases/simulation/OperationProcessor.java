package ar.edu.unp.madryn.livremarket.purchases.simulation;

import ar.edu.unp.madryn.livremarket.common.data.PurchaseManager;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.messages.Results;
import ar.edu.unp.madryn.livremarket.common.models.Purchase;
import ar.edu.unp.madryn.livremarket.common.server.ServerState;
import ar.edu.unp.madryn.livremarket.common.server.ServerStateManager;
import ar.edu.unp.madryn.livremarket.common.simulation.MessageProcessor;
import ar.edu.unp.madryn.livremarket.common.simulation.PendingOperation;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

public class OperationProcessor extends MessageProcessor {
    @Setter
    private PurchaseManager purchaseManager;
    @Setter
    private ServerStateManager serverStateManager;

    public OperationProcessor() {
    }

    @Override
    public PendingOperation processGeneralMessage(String operation, Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        ServerState serverState = new ServerState(purchaseID);

        Purchase purchase = null;

        if(!StringUtils.isEmpty(purchaseID)){
            /* Recuperar compra de la base de datos */
            purchase = this.purchaseManager.findPurchaseByID(purchaseID);
            if(purchase == null){
                Logging.error("Error: El ID de la compra es invalido!");
                return null;
            }

            /* Recuperar registro. */
            serverState = this.serverStateManager.getServerStateByID(purchaseID);
        }

        /* Actualizar estado interno. */
        switch (operation) {
            case Operations.INIT_PURCHASE:
                if(purchase != null){
                    Logging.error("Error: La compra ya existe!");
                    return null;
                }
                String clientID = data.get(MessageCommonFields.CLIENT_ID);
                String productID = data.get(MessageCommonFields.PRODUCT_ID);
                int amount = NumberUtils.toInt(data.get(MessageCommonFields.PRODUCT_AMOUNT), 1);

                purchase = new Purchase();
                purchase.setUnits(amount);

                // TODO Verificar si el cliente no existe

                purchase.setClientID(clientID);

                // TODO Buscar el producto segun su id

                purchase.setProductID(productID);

                this.purchaseManager.storePurchase(purchase);

                /* Actualizacion del estado */
                serverState.saveData(MessageCommonFields.PURCHASE_ID, purchase.getId());
                serverState.saveData(MessageCommonFields.CLIENT_ID, clientID);
                serverState.saveData(MessageCommonFields.PRODUCT_ID, productID);
                serverState.saveData(MessageCommonFields.PRODUCT_AMOUNT, String.valueOf(amount));

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

        if (serverState == null) {
            serverState = new ServerState(purchaseID);
        }

        /* Actualizar estado interno. */
        switch (id) {
            case Results.DELIVERY_COST_REFERENCE_ID:
                if(serverState.containsData(MessageCommonFields.DELIVERY_COST)) {
                    // TODO Error de informacion duplicada
                    return null;
                }
                String deliveryCost = data.get(MessageCommonFields.DELIVERY_COST);

                serverState.saveData(MessageCommonFields.DELIVERY_COST, deliveryCost);
                break;
            case Results.INFRACTIONS_REFERENCE_ID:
                if(serverState.containsData(MessageCommonFields.HAS_INFRACTIONS)) {
                    // TODO Error de informacion duplicada
                    return null;
                }
                String infractionsResult = data.get(MessageCommonFields.HAS_INFRACTIONS);

                serverState.saveData(MessageCommonFields.HAS_INFRACTIONS, infractionsResult);
                break;
            case Results.PAYMENT_AUTHORIZATION_REFERENCE_ID:
                if(serverState.containsData(MessageCommonFields.AUTHORIZED_PAYMENT)) {
                    // TODO Error de informacion duplicada
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
