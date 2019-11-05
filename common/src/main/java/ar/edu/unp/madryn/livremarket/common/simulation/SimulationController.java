package ar.edu.unp.madryn.livremarket.common.simulation;

import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.sm.StateMachine;
import ar.edu.unp.madryn.livremarket.common.sm.Template;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class SimulationController {
    @Setter
    private Template smTemplate;
    @Setter
    private DataProvider dataProvider;
    @Setter
    private MessageProcessor messageProcessor;
    @Setter
    private String stateCollectionName;

    private static SimulationController instance;

    public static SimulationController getInstance() {
        if(instance == null){
            instance = new SimulationController();
        }

        return instance;
    }

    private SimulationController() {

    }

    public boolean init(){
        return true;
    }

    public boolean step(){
        /*
        Buscar si hay una operacion pendiente
        Si hay una operacion pendiente
            Realizar un paso en la operacion
        Sino
            Si hay mensajes sin procesar
                Obtener el mensaje mas viejo
                Realizar un primer paso
                Si aun tiene pasos por realizar
                    Guardar como operacion pendiente
         */

        /* Obtener operacion pendiente */
        PendingOperation pendingOperation = this.dataProvider.getFirstElementInCollection(Definitions.CURRENT_OPERATION_DOCUMENT_NAME, PendingOperation.class);
        if(pendingOperation == null){
            /* Obtener mensajes sin procesar */
            Map<String,String> pendingMessage = this.dataProvider.getFirstDataInCollection(Definitions.PENDING_MESSAGES_COLLECTION_NAME);
            if(!MapUtils.isEmpty(pendingMessage)){
                /* Procesar mensaje */
                pendingOperation = this.messageProcessor.processMessage(pendingMessage);
            }
        }

        if(pendingOperation == null){
            return false;
        }

        /* Realizar un paso en la operacion pendiente */
        boolean hasNextStep = makeStep(pendingOperation);

        /* Quitar la operacion pendiente luego de ser ejecutada */
        this.dataProvider.clearCollectionContent(Definitions.CURRENT_OPERATION_DOCUMENT_NAME);

        if(hasNextStep){
            /* Actualizar operacion pendiente */
            this.dataProvider.insertElement(pendingOperation, Definitions.CURRENT_OPERATION_DOCUMENT_NAME);
        }

        return true;
    }

    private boolean makeStep(PendingOperation pendingOperation){
        Map<String,String> pendingOperationData = pendingOperation.getData();

        String purchaseID = pendingOperationData.get(MessageCommonFields.PURCHASE_ID);

        Map<String,String> storedState = new HashMap<>();

        if(!StringUtils.isEmpty(purchaseID)){
            /* Recuperar registro de la base de datos. */
            storedState = this.dataProvider.getDataFromCollectionByField(this.stateCollectionName, MessageCommonFields.PURCHASE_ID, purchaseID);
        }

        boolean hasNoState = MapUtils.isEmpty(storedState);

        State currentState;

        String currentStateName = pendingOperation.getCurrentState();

        if(!StringUtils.isEmpty(currentStateName)){
            currentState = smTemplate.searchStateByIdentifier(currentStateName);
            if(currentState == null){
                currentState = smTemplate.getInitialState();
            }
        }
        else {
            currentState = smTemplate.getInitialState();
        }

        StateMachine machine = new StateMachine(smTemplate, currentState);

        machine.addData(pendingOperation.getData());

        /* Intentar transicionar la maquina de estados una vez */
        machine.doStep();

        boolean result = machine.canDoStep();

        Map<String,String> machineData = machine.getData();

        /* Actualizar la operacion pendiente */
        pendingOperation.addData(machineData);
        pendingOperation.setCurrentState(machine.getCurrentState().getIdentifier());

        /* Persistencia del estado */

        /* Actualizar estado con lo modificado por la maquina de estados */
        storedState.putAll(machine.getData());
        storedState.put(MessageCommonFields.CURRENT_STATE, machine.getCurrentState().getIdentifier());

        /* Guardar estado */
        if(hasNoState) {
            this.dataProvider.insertElement(storedState, Definitions.PURCHASES_STATE_COLLECTION_NAME);
        }
        else{
            this.dataProvider.updateElement(MessageCommonFields.PURCHASE_ID, purchaseID, storedState, this.stateCollectionName);
        }

        return result;
    }
}
