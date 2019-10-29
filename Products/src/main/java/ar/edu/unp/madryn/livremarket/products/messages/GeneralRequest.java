package ar.edu.unp.madryn.livremarket.products.messages;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.messages.types.Request;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.sm.StateMachine;
import ar.edu.unp.madryn.livremarket.common.sm.Template;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.products.utils.LocalDefinitions;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

public class GeneralRequest extends Request {
    @Setter
    private CommunicationHandler communicationHandler;
    @Setter
    private ConfigurationSection simulationConfiguration;

    @Setter
    private DataProvider stateDataProvider;
    @Setter
    private Template smTemplate;

    @Override
    public void execute(String operation, Map<String, String> data) {
        System.out.println("Llego la operación general '" + operation + "' con los datos: " + data);
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        /* Recuperar registro de la base de datos. */
        Map<String,String> storedState = this.stateDataProvider.getDataFromCollectionByField(Definitions.PRODUCTS_STATE_COLLECTION_NAME, MessageCommonFields.PURCHASE_ID, purchaseID);

        boolean isNewPurchase = false;

        if(MapUtils.isEmpty(storedState)){
            storedState = new HashMap<>();
            isNewPurchase = true;
        }

        /* Actualizar estado interno. */
        switch (operation) {
            case Operations.PRODUCT_RESERVATION_OPERATION:
                if(!storedState.containsKey(MessageCommonFields.PURCHASE_ID)) {
                    storedState.put(MessageCommonFields.PURCHASE_ID, purchaseID);
                }

                if(!storedState.containsKey(MessageCommonFields.PRODUCT_ID)) {
                    String productID = data.get(MessageCommonFields.PRODUCT_ID);
                    storedState.put(MessageCommonFields.PRODUCT_ID, productID);
                }

                break;
        }

        State currentState;

        if(storedState.containsKey(Definitions.SM_CURRENT_STATE_REFERENCE)){
            currentState = smTemplate.searchStateByIdentifier(storedState.get(Definitions.SM_CURRENT_STATE_REFERENCE));
            if(currentState == null){
                currentState = smTemplate.getInitialState();
            }
        }
        else {
            currentState = smTemplate.getInitialState();
        }

        StateMachine machine = new StateMachine(smTemplate, currentState);

        machine.addData(storedState);

        /* Intentar trasicionar la maquina de estados. */
        while(machine.canDoStep()){
            machine.doStep();
        }

        /* Actualizar estado con lo modificado por la maquina de estados */
        storedState.putAll(machine.getData());
        storedState.put(MessageCommonFields.CURRENT_STATE, machine.getCurrentState().getIdentifier());

        /* Guardar estado */
        if(isNewPurchase) {
            this.stateDataProvider.insertElement(storedState, Definitions.PRODUCTS_STATE_COLLECTION_NAME);
        }
        else{
            this.stateDataProvider.updateElement(MessageCommonFields.PURCHASE_ID,
                    purchaseID,
                    storedState,
                    Definitions.PRODUCTS_STATE_COLLECTION_NAME);
        }
    }
}
