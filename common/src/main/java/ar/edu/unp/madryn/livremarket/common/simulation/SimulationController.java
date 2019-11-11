package ar.edu.unp.madryn.livremarket.common.simulation;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.server.ServerState;
import ar.edu.unp.madryn.livremarket.common.server.ServerStateManager;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.sm.StateMachine;
import ar.edu.unp.madryn.livremarket.common.sm.Template;
import ar.edu.unp.madryn.livremarket.common.threads.StatePersistenceWorker;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulationController {
    private static final long DEFAULT_STATE_PERSISTENCE_TASK_INTERVAL_MINUTES = 1;

    @Setter
    private Template smTemplate;
    @Setter
    private DataProvider dataProvider;
    @Setter
    private MessageProcessor messageProcessor;
    @Getter
    private boolean automatic;
    @Setter
    private ConfigurationSection simulationConfiguration;
    @Setter
    private ServerStateManager serverStateManager;

    private static SimulationController instance;

    public static SimulationController getInstance() {
        if(instance == null){
            instance = new SimulationController();
        }

        return instance;
    }

    private SimulationController() {
        this.automatic = (StringUtils.equals(Definitions.AUTO_SIMULATION_ID,Definitions.DEFAULT_SIMULATOR_MODE));
    }

    public boolean init(){
        String mode = this.simulationConfiguration.getValue(Definitions.SIMULATION_MODE_CONFIG_ID);
        if(!StringUtils.isEmpty(mode)){
            this.automatic = (StringUtils.equals(Definitions.AUTO_SIMULATION_ID,mode));
        }

        System.out.println("Simulador inicializado, la simulacion sera " + ((this.automatic) ? "automatica" : "manual") + "!");

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        StatePersistenceWorker persistenceWorker = new StatePersistenceWorker();
        persistenceWorker.setServerStateManager(this.serverStateManager);

        // TODO Hacer que el intervalo de persistencia sea configurable
        executorService.schedule(persistenceWorker, DEFAULT_STATE_PERSISTENCE_TASK_INTERVAL_MINUTES, TimeUnit.MINUTES);

        return true;
    }

    public void execute(){
        if(!this.automatic){
            return;
        }

        while(this.step()) {
            ;
        }
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
            Map<String,String> pendingMessage = this.dataProvider.getFirstDataInCollection(Definitions.PENDING_MESSAGES_COLLECTION_NAME, true);
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

        ServerState serverState = this.serverStateManager.getServerStateByID(purchaseID);

        if(serverState == null){
            serverState = new ServerState(purchaseID);
            this.serverStateManager.addServerState(serverState);
        }

        State currentState;

        String currentStateName = serverState.getSingleData(MessageCommonFields.CURRENT_STATE);

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

        /* Persistencia del estado */

        /* Actualizar estado con lo modificado por la maquina de estados */
        serverState.saveData(machine.getData());
        serverState.saveData(MessageCommonFields.CURRENT_STATE, machine.getCurrentState().getIdentifier());

        return result;
    }
}
