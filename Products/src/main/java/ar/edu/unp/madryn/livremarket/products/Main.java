package ar.edu.unp.madryn.livremarket.products;

import ar.edu.unp.madryn.livremarket.common.clocks.VectorClockController;
import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.consistency.MarkManager;
import ar.edu.unp.madryn.livremarket.common.consistency.ServerSnapshotManager;
import ar.edu.unp.madryn.livremarket.common.consistency.SnapshotController;
import ar.edu.unp.madryn.livremarket.common.data.ProductManager;
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
import ar.edu.unp.madryn.livremarket.products.simulation.OperationProcessor;
import ar.edu.unp.madryn.livremarket.products.sm.*;
import ar.edu.unp.madryn.livremarket.products.utils.LocalDefinitions;

public class Main {
    public static void main(String[] args) {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        ConfigurationSection connectionConfiguration = configurationManager.loadConfiguration(Definitions.CONNECTION_CONFIGURATION_FILE, ConfigurationSection.CONFIGURATION_FOLDER);
        if (connectionConfiguration == null) {
            Logging.error("Error: La configuracion de la conexion a la base de datos no existe!");
            return;
        }

        ConfigurationSection simulationConfiguration = configurationManager.loadConfiguration(Definitions.SIMULATION_CONFIGURATION_FILE, ConfigurationSection.CONFIGURATION_FOLDER);
        if (simulationConfiguration == null) {
            Logging.error("Error: La configuracion de la simulacion no existe!");
            return;
        }

        MessageHandlerManager messageHandlerManager = MessageHandlerManager.getInstance();

        CommunicationHandler communicationHandler = CommunicationHandler.getInstance();

        MessageWorker.setMessageHandlerManager(messageHandlerManager);
        MessageWorker.setServerID(Definitions.PRODUCTS_SERVER_NAME);

        VectorClockController vectorClockController = new VectorClockController(Definitions.PRODUCTS_SERVER_NAME);
        MessageWorker.setVectorClockController(vectorClockController);

        MessagePersistence messagePersistence = new MessagePersistence();

        messageHandlerManager.registerHandler(messagePersistence, MessageType.GENERAL, MessageType.RESULT);

        ControlMessage controlMessage = new ControlMessage();

        messageHandlerManager.registerHandler(controlMessage, MessageType.CONTROL);

        MarkMessage markMessage = new MarkMessage();

        messageHandlerManager.registerHandler(markMessage, MessageType.MARK);

        if (!communicationHandler.connect()) {
            Logging.error("No se pudo establecer conexion con el servidor AMQP!");
            return;
        }

        DataProviderFactory dataProviderFactory = DataProviderFactory.getInstance();

        DataProvider commonDataProvider = dataProviderFactory.getProviderInstance(connectionConfiguration, Definitions.COMMON_DATABASE_NAME);
        DataProvider productsDataProvider = dataProviderFactory.getProviderInstance(connectionConfiguration, Definitions.PRODUCTS_SERVER_NAME);

        if(!commonDataProvider.connect() || !productsDataProvider.connect()){
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

        ReservedProductState reservedProductState = new ReservedProductState();
        ProductManager productManager = ProductManager.getInstance();
        reservedProductState.setProductManager(productManager);
        smTemplate.addState(reservedProductState);

        ReportedInfractionsState reportedInfractionsState = new ReportedInfractionsState();
        smTemplate.addState(reportedInfractionsState);

        ReportedPaymentState reportedPaymentState = new ReportedPaymentState();
        smTemplate.addState(reportedPaymentState);

        ReleasingProductState releasingProductState = new ReleasingProductState();
        releasingProductState.setProductManager(productManager);
        smTemplate.addState(releasingProductState);

        NoInfractionsState noInfractionsState = new NoInfractionsState();
        smTemplate.addState(noInfractionsState);

        AuthorizedPaymentState authorizedPaymentState = new AuthorizedPaymentState();
        smTemplate.addState(authorizedPaymentState);

        WaitingBookedShipmentState waitingBookedShipmentState = new WaitingBookedShipmentState();
        smTemplate.addState(waitingBookedShipmentState);

        SendingProductState sendingProductState = new SendingProductState();
        smTemplate.addState(sendingProductState);

        /* Transiciones */
        smTemplate.addTransition(initialState, reservedProductState, data -> Conditions.isMapBooleanTrue(data, LocalDefinitions.PRODUCT_RESERVATION_REQUESTED_FIELD));
        smTemplate.addTransition(reservedProductState, reportedInfractionsState, data -> Conditions.mapContainsKey(data, MessageCommonFields.HAS_INFRACTIONS));
        smTemplate.addTransition(reportedInfractionsState, reportedPaymentState, data -> Conditions.mapContainsKey(data, MessageCommonFields.AUTHORIZED_PAYMENT));
        smTemplate.addTransition(reportedPaymentState, releasingProductState, data -> Conditions.isMapBooleanTrue(data, MessageCommonFields.HAS_INFRACTIONS));
        smTemplate.addTransition(reportedPaymentState, noInfractionsState, data -> Conditions.isMapBooleanFalse(data, MessageCommonFields.HAS_INFRACTIONS));
        smTemplate.addTransition(noInfractionsState, releasingProductState, data -> Conditions.isMapBooleanFalse(data, MessageCommonFields.AUTHORIZED_PAYMENT));
        smTemplate.addTransition(noInfractionsState, authorizedPaymentState, data -> Conditions.isMapBooleanTrue(data, MessageCommonFields.AUTHORIZED_PAYMENT));
        smTemplate.addTransition(releasingProductState, finalState, data -> Conditions.isMapBooleanFalse(data, LocalDefinitions.RESERVED_PRODUCT_FIELD));
        smTemplate.addTransition(authorizedPaymentState, waitingBookedShipmentState, data -> Conditions.isMapBooleanTrue(data, MessageCommonFields.NEEDS_SHIPPING));
        smTemplate.addTransition(waitingBookedShipmentState, sendingProductState, data -> Conditions.isMapBooleanTrue(data, MessageCommonFields.BOOKED_SHIPPING));
        smTemplate.addTransition(authorizedPaymentState, finalState, data -> Conditions.isMapBooleanFalse(data, MessageCommonFields.NEEDS_SHIPPING));
        smTemplate.addTransition(sendingProductState, finalState, data -> Conditions.mapContainsKey(data, LocalDefinitions.PRODUCT_SENT_FIELD));

        /* Datos faltante dentro del manejador de request generales */
        controlMessage.setCommunicationHandler(communicationHandler);

        messagePersistence.setDataProvider(productsDataProvider);

        ServerStateManager serverStateManager = ServerStateManager.getInstance();
        serverStateManager.setDataProvider(productsDataProvider);
        serverStateManager.setStateCollectionName(Definitions.PRODUCTS_STATE_COLLECTION_NAME);
        serverStateManager.setIdField(MessageCommonFields.PURCHASE_ID);

        operationProcessor.setServerStateManager(serverStateManager);

        SimulationController simulationController = SimulationController.getInstance();

        simulationController.setMessageProcessor(operationProcessor);
        simulationController.setDataProvider(productsDataProvider);
        simulationController.setSmTemplate(smTemplate);
        simulationController.setServerStateManager(serverStateManager);
        simulationController.setSimulationConfiguration(simulationConfiguration);

        simulationController.init();

        messagePersistence.setSimulationController(simulationController);

        controlMessage.setServerStateManager(serverStateManager);
        controlMessage.setSimulationController(simulationController);

        productManager.setDataProvider(commonDataProvider);

        productManager.load();

        communicationHandler.registerReceiver(Definitions.PRODUCTS_SERVER_NAME);

        /* Corte consistente */
        ServerSnapshotManager serverSnapshotManager = new ServerSnapshotManager();
        ServerSnapshotManager.setDataProvider(productsDataProvider);
        serverSnapshotManager.setServerStateManager(serverStateManager);

        SnapshotController snapshotController = new SnapshotController(Definitions.PRODUCTS_SERVER_NAME);
        snapshotController.addOtherServer(Definitions.DELIVERIES_SERVER_NAME);
        snapshotController.addOtherServer(Definitions.INFRACTIONS_SERVER_NAME);
        snapshotController.addOtherServer(Definitions.PAYMENTS_SERVER_NAME);
        snapshotController.addOtherServer(Definitions.PURCHASES_SERVER_NAME);
        snapshotController.setServerSnapshotManager(serverSnapshotManager);

        MarkManager.setCommunicationHandler(communicationHandler);

        controlMessage.setSnapshotController(snapshotController);
        messagePersistence.setSnapshotController(snapshotController);
        markMessage.setSnapshotController(snapshotController);

        Logging.info("Escuchando mensajes (Ctrl + C para cerrar)...");
    }
}
