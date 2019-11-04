package ar.edu.unp.madryn.livremarket.purchases.messages;

import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.Results;
import ar.edu.unp.madryn.livremarket.common.messages.types.Information;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.sm.StateMachine;
import ar.edu.unp.madryn.livremarket.common.sm.Template;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

public class ResultInformation extends Information {

    @Setter
    private DataProvider stateDataProvider;
    @Setter
    private Template smTemplate;

    @Override
    public void process(String id, Map<String, String> data) {
        System.out.println("Llego informacion con id '" + id + "' y con los datos: " + data);
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        /* Recuperar registro de la base de datos. */
        Map<String, String> storedState = this.stateDataProvider.getDataFromCollectionByField(Definitions.PRODUCTS_STATE_COLLECTION_NAME, MessageCommonFields.PURCHASE_ID, purchaseID);

        boolean isNewPurchase = false;

        if (MapUtils.isEmpty(storedState)) {
            storedState = new HashMap<>();
            isNewPurchase = true;
        }

        /* Actualizar estado interno. */
        switch (id) {
            case Results.DELIVERY_COST_REFERENCE_ID:
                if(storedState.containsKey(MessageCommonFields.DELIVERY_COST)) {
                    // TODO Error de informacion duplicada
                    return;
                }
                String deliveryCost = data.get(MessageCommonFields.DELIVERY_COST);

                storedState.put(MessageCommonFields.DELIVERY_COST, deliveryCost);
                break;
            case Results.INFRACTIONS_REFERENCE_ID:
                if(storedState.containsKey(MessageCommonFields.HAS_INFRACTIONS)) {
                    // TODO Error de informacion duplicada
                    return;
                }
                String infractionsResult = data.get(MessageCommonFields.HAS_INFRACTIONS);

                storedState.put(MessageCommonFields.HAS_INFRACTIONS, infractionsResult);
                break;
            case Results.PAYMENT_AUTHORIZATION_REFERENCE_ID:
                if(storedState.containsKey(MessageCommonFields.AUTHORIZED_PAYMENT)) {
                    // TODO Error de informacion duplicada
                    return;
                }
                String paymentResult = data.get(MessageCommonFields.AUTHORIZED_PAYMENT);

                storedState.put(MessageCommonFields.AUTHORIZED_PAYMENT, paymentResult);
                break;
            default:
                System.err.println("Error: ID de informacion '" + id + "' no reconocido!");
                return;
        }

        State currentState;

        if (storedState.containsKey(Definitions.SM_CURRENT_STATE_REFERENCE)) {
            currentState = smTemplate.searchStateByIdentifier(storedState.get(Definitions.SM_CURRENT_STATE_REFERENCE));
            if (currentState == null) {
                currentState = smTemplate.getInitialState();
            }
        } else {
            currentState = smTemplate.getInitialState();
        }

        StateMachine machine = new StateMachine(smTemplate, currentState);

        machine.addData(storedState);

        /* Intentar trasicionar la maquina de estados. */
        while (machine.canDoStep()) {
            machine.doStep();
        }

        /* Actualizar estado con lo modificado por la maquina de estados */
        storedState.putAll(machine.getData());
        storedState.put(MessageCommonFields.CURRENT_STATE, machine.getCurrentState().getIdentifier());

        /* Guardar estado */
        if (isNewPurchase) {
            this.stateDataProvider.insertElement(storedState, Definitions.PRODUCTS_STATE_COLLECTION_NAME);
        } else {
            this.stateDataProvider.updateElement(MessageCommonFields.PURCHASE_ID,
                    purchaseID,
                    storedState,
                    Definitions.PRODUCTS_STATE_COLLECTION_NAME);
        }
    }
}
