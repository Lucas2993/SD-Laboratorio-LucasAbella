package ar.edu.unp.madryn.livremarket.purchases.simulation;

import ar.edu.unp.madryn.livremarket.common.data.PurchaseManager;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.messages.Results;
import ar.edu.unp.madryn.livremarket.common.models.Purchase;
import ar.edu.unp.madryn.livremarket.common.simulation.MessageProcessor;
import ar.edu.unp.madryn.livremarket.common.simulation.PendingOperation;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;

public class OperationProcessor extends MessageProcessor {
    @Setter
    private PurchaseManager purchaseManager;
    @Setter
    private DataProvider stateDataProvider;

    public OperationProcessor() {
    }

    @Override
    public PendingOperation processGeneralMessage(String operation, Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        Map<String,String> storedState = new HashMap<>();

        Purchase purchase = null;

        if(!StringUtils.isEmpty(purchaseID)){
            /* Recuperar compra de la base de datos */
            purchase = this.purchaseManager.findProductByID(purchaseID);
            if(purchase == null){
                System.err.println("Error: El ID de la compra es invalido!");
                return null;
            }

            /* Recuperar registro de la base de datos. */
            storedState = this.stateDataProvider.getDataFromCollectionByField(Definitions.PURCHASES_STATE_COLLECTION_NAME, MessageCommonFields.PURCHASE_ID, purchaseID);
        }

        /* Actualizar estado interno. */
        switch (operation) {
            case Operations.INIT_PURCHASE:
                if(purchase != null){
                    System.err.println("Error: La compra ya existe!");
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

                this.purchaseManager.storeProduct(purchase);

                /* Actualizacion del estado */
                storedState.put(MessageCommonFields.PURCHASE_ID, purchase.getId());
                storedState.put(MessageCommonFields.CLIENT_ID, clientID);
                storedState.put(MessageCommonFields.PRODUCT_ID, productID);
                storedState.put(MessageCommonFields.PRODUCT_AMOUNT, String.valueOf(amount));

                break;
            default:
                System.err.println("Error: Operacion '" + operation + "' no reconocida!");
                return null;
        }

        PendingOperation pendingOperation = new PendingOperation();

        pendingOperation.setData(storedState);

        return pendingOperation;
    }

    @Override
    public PendingOperation processResultMessage(String id, Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        /* Recuperar registro de la base de datos. */
        Map<String, String> storedState = this.stateDataProvider.getDataFromCollectionByField(Definitions.PRODUCTS_STATE_COLLECTION_NAME, MessageCommonFields.PURCHASE_ID, purchaseID);

        if (MapUtils.isEmpty(storedState)) {
            storedState = new HashMap<>();
        }

        /* Actualizar estado interno. */
        switch (id) {
            case Results.DELIVERY_COST_REFERENCE_ID:
                if(storedState.containsKey(MessageCommonFields.DELIVERY_COST)) {
                    // TODO Error de informacion duplicada
                    return null;
                }
                String deliveryCost = data.get(MessageCommonFields.DELIVERY_COST);

                storedState.put(MessageCommonFields.DELIVERY_COST, deliveryCost);
                break;
            case Results.INFRACTIONS_REFERENCE_ID:
                if(storedState.containsKey(MessageCommonFields.HAS_INFRACTIONS)) {
                    // TODO Error de informacion duplicada
                    return null;
                }
                String infractionsResult = data.get(MessageCommonFields.HAS_INFRACTIONS);

                storedState.put(MessageCommonFields.HAS_INFRACTIONS, infractionsResult);
                break;
            case Results.PAYMENT_AUTHORIZATION_REFERENCE_ID:
                if(storedState.containsKey(MessageCommonFields.AUTHORIZED_PAYMENT)) {
                    // TODO Error de informacion duplicada
                    return null;
                }
                String paymentResult = data.get(MessageCommonFields.AUTHORIZED_PAYMENT);

                storedState.put(MessageCommonFields.AUTHORIZED_PAYMENT, paymentResult);
                break;
            default:
                System.err.println("Error: ID de informacion '" + id + "' no reconocido!");
                return null;
        }

        PendingOperation pendingOperation = new PendingOperation();

        pendingOperation.setData(storedState);

        return pendingOperation;
    }
}
