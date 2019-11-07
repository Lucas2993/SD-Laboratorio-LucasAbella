package ar.edu.unp.madryn.livremarket.infractions;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.db.DataProviderFactory;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.types.ControlMessage;
import ar.edu.unp.madryn.livremarket.common.messages.types.MessagePersistence;
import ar.edu.unp.madryn.livremarket.common.simulation.SimulationController;
import ar.edu.unp.madryn.livremarket.common.sm.FinalState;
import ar.edu.unp.madryn.livremarket.common.sm.InitialState;
import ar.edu.unp.madryn.livremarket.common.sm.Template;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.infractions.simulation.OperationProcessor;
import ar.edu.unp.madryn.livremarket.infractions.sm.ReportingInfractionsState;
import ar.edu.unp.madryn.livremarket.infractions.sm.ResolvingInfractionsState;
import ar.edu.unp.madryn.livremarket.infractions.utils.LocalDefinitions;
import org.apache.commons.collections4.MapUtils;

public class Main {
    public static void main(String[] args) {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        ConfigurationSection simulationConfiguration = configurationManager.loadConfiguration(Definitions.SIMULATION_CONFIGURATION_FILE, ConfigurationSection.CONFIGURATION_FOLDER);
        if (simulationConfiguration == null) {
            System.err.println("Error: La configuracion de la simulacion no existe!");
            return;
        }

        ConfigurationSection connectionConfiguration = configurationManager.loadConfiguration(Definitions.CONNECTION_CONFIGURATION_FILE, ConfigurationSection.CONFIGURATION_FOLDER);
        if (connectionConfiguration == null) {
            System.err.println("Error: La configuracion de la conexion a la base de datos no existe!");
            return;
        }

        CommunicationHandler communicationHandler = CommunicationHandler.getInstance();

        MessagePersistence messagePersistence = new MessagePersistence();

        communicationHandler.registerHandler(messagePersistence, MessageType.GENERAL);

        ControlMessage controlMessage = new ControlMessage();

        communicationHandler.registerHandler(controlMessage, MessageType.CONTROL);

        if (!communicationHandler.connect()) {
            System.err.println("No se pudo establecer conexion con el servidor AMQP!");
            return;
        }

        DataProviderFactory dataProviderFactory = DataProviderFactory.getInstance();

        DataProvider infractionsDataProvider = dataProviderFactory.getProviderInstance(connectionConfiguration, Definitions.INFRACTIONS_SERVER_NAME);

        if(!infractionsDataProvider.connect()){
            System.err.println("No se pudo establecer conexion con el servidor de base de datos!");
            return;
        }

        OperationProcessor operationProcessor = new OperationProcessor();

        /* Maquina de estados */
        Template smTemplate = new Template();

        /* Estados */
        InitialState initialState = new InitialState();
        smTemplate.addState(initialState);

        FinalState finalState = new FinalState();
        smTemplate.addState(finalState);

        ResolvingInfractionsState resolvingInfractionsState = new ResolvingInfractionsState();
        resolvingInfractionsState.setSimulationConfiguration(simulationConfiguration);
        smTemplate.addState(resolvingInfractionsState);

        ReportingInfractionsState reportingInfractionsState = new ReportingInfractionsState();
        reportingInfractionsState.setCommunicationHandler(communicationHandler);
        smTemplate.addState(reportingInfractionsState);

        /* Transiciones */
        smTemplate.addTransition(initialState, resolvingInfractionsState, data -> MapUtils.getBoolean(data, LocalDefinitions.REQUESTED_INFRACTIONS_FIELD));
        smTemplate.addTransition(resolvingInfractionsState, reportingInfractionsState, data -> data.containsKey(MessageCommonFields.HAS_INFRACTIONS));
        smTemplate.addTransition(reportingInfractionsState, finalState, data -> MapUtils.getBoolean(data, LocalDefinitions.REPORTED_INFRACTIONS_FIELD));

        /* Datos faltante dentro del manejador de request generales */
        messagePersistence.setDataProvider(infractionsDataProvider);

        operationProcessor.setStateDataProvider(infractionsDataProvider);

        SimulationController simulationController = SimulationController.getInstance();

        simulationController.setMessageProcessor(operationProcessor);
        simulationController.setDataProvider(infractionsDataProvider);
        simulationController.setSmTemplate(smTemplate);
        simulationController.setStateCollectionName(Definitions.INFRACTIONS_STATE_COLLECTION_NAME);
        simulationController.setSimulationConfiguration(simulationConfiguration);

        simulationController.init();

        messagePersistence.setSimulationController(simulationController);

        communicationHandler.registerReceiver(Definitions.INFRACTIONS_SERVER_NAME);

        System.out.println("Escuchando mensajes (Ctrl + C para cerrar)...");
    }
}
