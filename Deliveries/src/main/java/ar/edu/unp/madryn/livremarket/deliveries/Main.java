package ar.edu.unp.madryn.livremarket.deliveries;

import ar.edu.unp.madryn.livremarket.common.clocks.VectorClockController;
import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.consistency.MarkManager;
import ar.edu.unp.madryn.livremarket.common.consistency.ServerSnapshotManager;
import ar.edu.unp.madryn.livremarket.common.consistency.SnapshotController;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.db.DataProviderFactory;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandlerManager;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.types.ControlMessage;
import ar.edu.unp.madryn.livremarket.common.messages.types.MarkMessage;
import ar.edu.unp.madryn.livremarket.common.messages.types.MessagePersistence;
import ar.edu.unp.madryn.livremarket.common.server.ServerStateManager;
import ar.edu.unp.madryn.livremarket.common.simulation.SimulationController;
import ar.edu.unp.madryn.livremarket.common.sm.FinalState;
import ar.edu.unp.madryn.livremarket.common.sm.InitialState;
import ar.edu.unp.madryn.livremarket.common.sm.Template;
import ar.edu.unp.madryn.livremarket.common.threads.MessageWorker;
import ar.edu.unp.madryn.livremarket.common.utils.Conditions;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import ar.edu.unp.madryn.livremarket.deliveries.simulation.OperationProcessor;
import ar.edu.unp.madryn.livremarket.deliveries.sm.*;
import ar.edu.unp.madryn.livremarket.deliveries.utils.LocalDefinitions;

public class Main {
    public static void main(String[] args) {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        ConfigurationSection simulationConfiguration = configurationManager.loadConfiguration(Definitions.SIMULATION_CONFIGURATION_FILE, ConfigurationSection.CONFIGURATION_FOLDER);
        if (simulationConfiguration == null) {
            Logging.error("Error: La configuracion de la simulacion no existe!");
            return;
        }

        ConfigurationSection connectionConfiguration = configurationManager.loadConfiguration(Definitions.CONNECTION_CONFIGURATION_FILE, ConfigurationSection.CONFIGURATION_FOLDER);
        if (connectionConfiguration == null) {
            Logging.error("Error: La configuracion de la conexion a la base de datos no existe!");
            return;
        }

        MessageHandlerManager messageHandlerManager = MessageHandlerManager.getInstance();

        CommunicationHandler communicationHandler = CommunicationHandler.getInstance();

        MessageWorker.setMessageHandlerManager(messageHandlerManager);
        MessageWorker.setServerID(Definitions.DELIVERIES_SERVER_NAME);

        VectorClockController vectorClockController = new VectorClockController(Definitions.DELIVERIES_SERVER_NAME);
        MessageWorker.setVectorClockController(vectorClockController);

        MessagePersistence messagePersistence = new MessagePersistence();

        messageHandlerManager.registerHandler(messagePersistence, MessageType.GENERAL);

        ControlMessage controlMessage = new ControlMessage();

        messageHandlerManager.registerHandler(controlMessage, MessageType.CONTROL);

        MarkMessage markMessage = new MarkMessage();

        messageHandlerManager.registerHandler(markMessage, MessageType.MARK);

        if (!communicationHandler.connect()) {
            Logging.error("No se pudo establecer conexion con el servidor AMQP!");
            return;
        }

        DataProviderFactory dataProviderFactory = DataProviderFactory.getInstance();

        DataProvider deliveriesDataProvider = dataProviderFactory.getProviderInstance(connectionConfiguration, Definitions.DELIVERIES_SERVER_NAME);

        if (!deliveriesDataProvider.connect()) {
            Logging.error("No se pudo establecer conexion con el servidor de base de datos!");
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

        CalculatingCostState calculatingCostState = new CalculatingCostState();
        calculatingCostState.setSimulationConfiguration(simulationConfiguration);
        smTemplate.addState(calculatingCostState);

        ReportingCostState reportingCostState = new ReportingCostState();
        reportingCostState.setCommunicationHandler(communicationHandler);
        smTemplate.addState(reportingCostState);

        ReportedCostState reportedCostState = new ReportedCostState();
        smTemplate.addState(reportedCostState);

        BookingDeliveryState bookingDeliveryState = new BookingDeliveryState();
        bookingDeliveryState.setDataProvider(deliveriesDataProvider);
        smTemplate.addState(bookingDeliveryState);

        ReportingBookedDeliveryState reportingBookedDeliveryState = new ReportingBookedDeliveryState();
        reportingBookedDeliveryState.setCommunicationHandler(communicationHandler);
        smTemplate.addState(reportingBookedDeliveryState);

        /* Transiciones */
        smTemplate.addTransition(initialState, calculatingCostState, data -> Conditions.isMapBooleanTrue(data, LocalDefinitions.REQUESTED_COST_FIELD));
        smTemplate.addTransition(calculatingCostState, reportingCostState, data -> Conditions.mapContainsKey(data, MessageCommonFields.DELIVERY_COST));
        smTemplate.addTransition(reportingCostState, reportedCostState, data -> Conditions.isMapBooleanTrue(data, LocalDefinitions.REPORTED_COST_FIELD));
        smTemplate.addTransition(reportedCostState, bookingDeliveryState, data -> Conditions.isMapBooleanTrue(data, LocalDefinitions.REQUESTED_DELIVERY_BOOK_FIELD));
        smTemplate.addTransition(bookingDeliveryState, reportingBookedDeliveryState, data -> Conditions.isMapBooleanTrue(data, LocalDefinitions.BOOKED_DELIVERY_FIELD));
        smTemplate.addTransition(reportingBookedDeliveryState, finalState, data -> Conditions.isMapBooleanTrue(data, LocalDefinitions.REPORTED_BOOKED_DELIVERY_FIELD));


        /* Datos faltante dentro del manejador de request generales */
        controlMessage.setCommunicationHandler(communicationHandler);

        messagePersistence.setDataProvider(deliveriesDataProvider);

        ServerStateManager serverStateManager = ServerStateManager.getInstance();
        serverStateManager.setDataProvider(deliveriesDataProvider);
        serverStateManager.setStateCollectionName(Definitions.DELIVERIES_STATE_COLLECTION_NAME);
        serverStateManager.setIdField(MessageCommonFields.PURCHASE_ID);

        operationProcessor.setServerStateManager(serverStateManager);

        SimulationController simulationController = SimulationController.getInstance();

        simulationController.setMessageProcessor(operationProcessor);
        simulationController.setDataProvider(deliveriesDataProvider);
        simulationController.setSmTemplate(smTemplate);
        simulationController.setServerStateManager(serverStateManager);
        simulationController.setSimulationConfiguration(simulationConfiguration);

        simulationController.init();

        messagePersistence.setSimulationController(simulationController);

        controlMessage.setServerStateManager(serverStateManager);
        controlMessage.setSimulationController(simulationController);

        communicationHandler.registerReceiver(Definitions.DELIVERIES_SERVER_NAME);

        /* Corte consistente */
        ServerSnapshotManager serverSnapshotManager = new ServerSnapshotManager();
        ServerSnapshotManager.setDataProvider(deliveriesDataProvider);
        serverSnapshotManager.setServerStateManager(serverStateManager);

        SnapshotController snapshotController = new SnapshotController(Definitions.DELIVERIES_SERVER_NAME);
        snapshotController.addOtherServer(Definitions.INFRACTIONS_SERVER_NAME);
        snapshotController.addOtherServer(Definitions.PAYMENTS_SERVER_NAME);
        snapshotController.addOtherServer(Definitions.PRODUCTS_SERVER_NAME);
        snapshotController.addOtherServer(Definitions.PURCHASES_SERVER_NAME);
        snapshotController.setServerSnapshotManager(serverSnapshotManager);

        MarkManager.setCommunicationHandler(communicationHandler);

        controlMessage.setSnapshotController(snapshotController);
        messagePersistence.setSnapshotController(snapshotController);
        markMessage.setSnapshotController(snapshotController);

        Logging.info("Escuchando mensajes (Ctrl + C para cerrar)...");
    }
}
