package ar.edu.unp.madryn.livremarket.infractions.simulation;

import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.simulation.MessageProcessor;
import ar.edu.unp.madryn.livremarket.common.simulation.PendingOperation;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.infractions.utils.LocalDefinitions;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

public class OperationProcessor extends MessageProcessor {
    @Setter
    private DataProvider stateDataProvider;

    public OperationProcessor() {
    }

    @Override
    public PendingOperation processGeneralMessage(String operation, Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        /* Recuperar registro de la base de datos. */
        Map<String,String> storedState = this.stateDataProvider.getDataFromCollectionByField(Definitions.INFRACTIONS_STATE_COLLECTION_NAME, MessageCommonFields.PURCHASE_ID, purchaseID);

        if(MapUtils.isEmpty(storedState)){
            storedState = new HashMap<>();
            storedState.put(MessageCommonFields.PURCHASE_ID, purchaseID);
        }

        switch (operation) {
            case Operations.GET_INFRACTIONS_OPERATION:
                System.out.println("Solicitud de infracciones recibida! (ID =" + purchaseID + ")");

                if(data.containsKey(LocalDefinitions.REQUESTED_INFRACTIONS_FIELD)){
                    System.err.println("Error: Operacion duplicada! (ID =" + purchaseID + ")");
                }

                storedState.put(LocalDefinitions.REQUESTED_INFRACTIONS_FIELD, String.valueOf(true));

                break;
            default:
                System.err.println("Error: Operacion '" + operation + "' no reconocida!");
        }

        PendingOperation pendingOperation = new PendingOperation();

        pendingOperation.setData(storedState);

        return pendingOperation;
    }

    @Override
    public PendingOperation processResultMessage(String id, Map<String, String> data) {
        return null;
    }
}