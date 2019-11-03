package ar.edu.unp.madryn.livremarket.purchases.messages;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.messages.types.Request;
import ar.edu.unp.madryn.livremarket.common.models.Client;
import ar.edu.unp.madryn.livremarket.common.models.Purchase;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.sm.StateMachine;
import ar.edu.unp.madryn.livremarket.common.sm.Template;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

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

        Map<String,String> storedState = new HashMap<>();

        Purchase purchase;

        if(!StringUtils.isEmpty(purchaseID)){
            /* Recuperar registro de la base de datos. */
             storedState = this.stateDataProvider.getDataFromCollectionByField(Definitions.PURCHASES_STATE_COLLECTION_NAME, MessageCommonFields.PURCHASE_ID, purchaseID);
             // TODO Buscar la compra
        }

        boolean isNewPurchase = MapUtils.isEmpty(storedState);

        /* Actualizar estado interno. */
        switch (operation) {
            case Operations.INIT_PURCHASE:
                String clientID = data.get(MessageCommonFields.CLIENT_ID);
                String productID = data.get(MessageCommonFields.PRODUCT_ID);
                int amount = NumberUtils.toInt(data.get(MessageCommonFields.PRODUCT_AMOUNT), 1);

                purchase = new Purchase();
                purchase.setUnits(amount);

                // TODO Verificar si el cliente no existe

                // TODO Si se creo hay que guardar el cliente

                purchase.setClientID(clientID);

                // TODO Buscar el producto segun su id

                purchase.setProductID(productID);

                // TODO Guardar compra en la base de datos

                /* Actualizacion del estado */
                storedState.put(MessageCommonFields.PURCHASE_ID, purchase.getId());
                storedState.put(MessageCommonFields.CLIENT_ID, clientID);
                storedState.put(MessageCommonFields.PRODUCT_ID, productID);
                storedState.put(MessageCommonFields.PRODUCT_AMOUNT, String.valueOf(amount));

                break;
            default:
                // TODO Error por tipo de mensaje no reconocido...
                return;
        }

        // TODO Si no hay id de compra quiere decir que el mensaje fue erroneo...

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
            this.stateDataProvider.insertElement(storedState, Definitions.PURCHASES_STATE_COLLECTION_NAME);
        }
        else{
            this.stateDataProvider.updateElement(MessageCommonFields.PURCHASE_ID,
                    purchaseID,
                    storedState,
                    Definitions.PRODUCTS_STATE_COLLECTION_NAME);
        }
    }
}
