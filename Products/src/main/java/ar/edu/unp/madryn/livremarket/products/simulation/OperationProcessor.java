package ar.edu.unp.madryn.livremarket.products.simulation;

import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.messages.Results;
import ar.edu.unp.madryn.livremarket.common.simulation.MessageProcessor;
import ar.edu.unp.madryn.livremarket.common.simulation.PendingOperation;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.products.utils.LocalDefinitions;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

public class OperationProcessor extends MessageProcessor {

    @Setter
    private DataProvider stateDataProvider;

    @Override
    public PendingOperation processGeneralMessage(String operation, Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        /* Recuperar registro de la base de datos. */
        Map<String,String> storedState = this.stateDataProvider.getDataFromCollectionByField(Definitions.PRODUCTS_STATE_COLLECTION_NAME, MessageCommonFields.PURCHASE_ID, purchaseID);

        if(MapUtils.isEmpty(storedState)){
            storedState = new HashMap<>();
            storedState.put(MessageCommonFields.PURCHASE_ID, purchaseID);
        }

        /* Actualizar estado interno. */
        switch (operation) {
            case Operations.PRODUCT_RESERVATION_OPERATION:
                if(storedState.containsKey(MessageCommonFields.PRODUCT_ID)) {
                    System.err.println("Error: Operacion duplicada!");
                    return null;
                }

                String productID = data.get(MessageCommonFields.PRODUCT_ID);
                storedState.put(MessageCommonFields.PRODUCT_ID, productID);
                storedState.put(LocalDefinitions.PRODUCT_RESERVATION_REQUESTED_FIELD, String.valueOf(true));
                break;
            case Operations.BOOK_SHIPMENT_OPERATION:
                if(storedState.containsKey(MessageCommonFields.BOOKED_SHIPPING)) {
                    System.err.println("Error: Operacion duplicada!");
                    return null;
                }

                // TODO Registrar envio en la base de datos
                storedState.put(MessageCommonFields.BOOKED_SHIPPING, String.valueOf(true));
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
            case Results.INFRACTIONS_REFERENCE_ID:
                if(storedState.containsKey(MessageCommonFields.HAS_INFRACTIONS)) {
                    System.err.println("Error: Informacion duplicada!");
                    return null;
                }
                String infractionsResult = data.get(MessageCommonFields.HAS_INFRACTIONS);

                storedState.put(MessageCommonFields.HAS_INFRACTIONS, infractionsResult);
                break;
            case Results.PAYMENT_AUTHORIZATION_REFERENCE_ID:
                if(storedState.containsKey(MessageCommonFields.AUTHORIZED_PAYMENT)) {
                    System.err.println("Error: Informacion duplicada!");
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
