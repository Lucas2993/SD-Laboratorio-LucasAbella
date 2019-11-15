package ar.edu.unp.madryn.livremarket.purchases;

import ar.edu.unp.madryn.livremarket.common.clocks.VectorClockController;
import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.data.PurchaseManager;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.db.DataProviderFactory;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandlerManager;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.types.ControlMessage;
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
import ar.edu.unp.madryn.livremarket.purchases.simulation.OperationProcessor;
import ar.edu.unp.madryn.livremarket.purchases.sm.*;
import ar.edu.unp.madryn.livremarket.purchases.utils.LocalDefinitions;

public class Main {
    public static void main(String [] args){
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

        VectorClockController vectorClockController = new VectorClockController(Definitions.PURCHASES_SERVER_NAME);
        MessageWorker.setVectorClockController(vectorClockController);

        OperationProcessor operationProcessor = new OperationProcessor();

        MessagePersistence messagePersistence = new MessagePersistence();

        messageHandlerManager.registerHandler(messagePersistence, MessageType.GENERAL, MessageType.RESULT);

        ControlMessage controlMessage = new ControlMessage();

        messageHandlerManager.registerHandler(controlMessage, MessageType.CONTROL);

        if (!communicationHandler.connect()) {
            Logging.error("No se pudo establecer conexion con el servidor AMQP!");
            return;
        }

        DataProviderFactory dataProviderFactory = DataProviderFactory.getInstance();

        DataProvider commonDataProvider = dataProviderFactory.getProviderInstance(connectionConfiguration, Definitions.COMMON_DATABASE_NAME);
        DataProvider purchasesDataProvider = dataProviderFactory.getProviderInstance(connectionConfiguration, Definitions.PURCHASES_SERVER_NAME);

        if(!commonDataProvider.connect() || !purchasesDataProvider.connect()){
            Logging.error("No se pudo establecer conexion con el servidor de base de datos!");
            return;
        }

        PurchaseManager purchaseManager = PurchaseManager.getInstance();

        /* Maquina de estados */
        Template smTemplate = new Template();

        /* Estados */
        InitialState initialState = new InitialState();
        smTemplate.addState(initialState);

        FinalState finalState = new FinalState();
        smTemplate.addState(finalState);

        SelectingProductState selectingProductState = new SelectingProductState();
        smTemplate.addState(selectingProductState);

        RequestingProductReservationState requestingProductReservationState = new RequestingProductReservationState();
        requestingProductReservationState.setCommunicationHandler(communicationHandler);
        smTemplate.addState(requestingProductReservationState);

        RequestingInfractionsState requestingInfractionsState = new RequestingInfractionsState();
        requestingInfractionsState.setCommunicationHandler(communicationHandler);
        smTemplate.addState(requestingInfractionsState);

        SelectingDeliveryState selectingDeliveryState = new SelectingDeliveryState();
        selectingDeliveryState.setPurchaseManager(purchaseManager);
        selectingDeliveryState.setSimulationConfiguration(simulationConfiguration);
        smTemplate.addState(selectingDeliveryState);

        RequestingDeliveryCostState requestingDeliveryCostState = new RequestingDeliveryCostState();
        requestingDeliveryCostState.setCommunicationHandler(communicationHandler);
        smTemplate.addState(requestingDeliveryCostState);

        SelectingPaymentState selectingPaymentState = new SelectingPaymentState();
        selectingPaymentState.setPurchaseManager(purchaseManager);
        selectingPaymentState.setSimulationConfiguration(simulationConfiguration);
        smTemplate.addState(selectingPaymentState);

        PurchaseConfirmedState purchaseConfirmedState = new PurchaseConfirmedState();
        smTemplate.addState(purchaseConfirmedState);

        ReportedInfractionsState reportedInfractionsState = new ReportedInfractionsState();
        smTemplate.addState(reportedInfractionsState);

        ReportingInfractionsState reportingInfractionsState = new ReportingInfractionsState();
        smTemplate.addState(reportingInfractionsState);

        RequestingPaymentAuthorizationState requestingPaymentAuthorizationState = new RequestingPaymentAuthorizationState();
        requestingPaymentAuthorizationState.setCommunicationHandler(communicationHandler);
        smTemplate.addState(requestingPaymentAuthorizationState);

        ReportedPaymentAuthorizationState reportedPaymentAuthorizationState = new ReportedPaymentAuthorizationState();
        smTemplate.addState(reportedPaymentAuthorizationState);

        ReportingRejectedPaymentState reportingRejectedPaymentState = new ReportingRejectedPaymentState();
        smTemplate.addState(reportingRejectedPaymentState);

        AuthorizedPaymentState authorizedPaymentState = new AuthorizedPaymentState();
        smTemplate.addState(authorizedPaymentState);

        RequestingShippingScheduleState requestingShippingScheduleState = new RequestingShippingScheduleState();
        requestingShippingScheduleState.setCommunicationHandler(communicationHandler);
        smTemplate.addState(requestingShippingScheduleState);

        PurchaseCompletedState purchaseCompletedState = new PurchaseCompletedState();
        smTemplate.addState(purchaseCompletedState);


        /* Transiciones */
        smTemplate.addTransition(initialState, selectingProductState, data -> Conditions.mapContainsKey(data, MessageCommonFields.CLIENT_ID));
        smTemplate.addTransition(selectingProductState, requestingProductReservationState, data -> Conditions.mapContainsKey(data, MessageCommonFields.PRODUCT_ID) && data.containsKey(MessageCommonFields.PRODUCT_AMOUNT));
        smTemplate.addTransition(requestingProductReservationState, requestingInfractionsState, data -> Conditions.isMapBooleanTrue(data, LocalDefinitions.PRODUCT_RESERVATION_REQUESTED_FIELD));
        smTemplate.addTransition(requestingInfractionsState, selectingDeliveryState, data -> Conditions.isMapBooleanTrue(data, LocalDefinitions.INFRACTIONS_REQUESTED_FIELD));
        smTemplate.addTransition(selectingDeliveryState, requestingDeliveryCostState, data -> Conditions.isMapBooleanTrue(data, MessageCommonFields.NEEDS_SHIPPING));
        smTemplate.addTransition(selectingDeliveryState, selectingPaymentState, data -> Conditions.isMapBooleanFalse(data, MessageCommonFields.NEEDS_SHIPPING));
        smTemplate.addTransition(requestingDeliveryCostState, selectingPaymentState, data -> Conditions.mapContainsKey(data, MessageCommonFields.DELIVERY_COST));
        smTemplate.addTransition(selectingPaymentState, purchaseConfirmedState, data -> Conditions.mapContainsKey(data, MessageCommonFields.PAYMENT_METHOD));
        smTemplate.addTransition(purchaseConfirmedState, reportedInfractionsState, data -> Conditions.mapContainsKey(data, MessageCommonFields.HAS_INFRACTIONS));
        smTemplate.addTransition(reportedInfractionsState, reportingInfractionsState, data -> Conditions.isMapBooleanTrue(data, MessageCommonFields.HAS_INFRACTIONS));
        smTemplate.addTransition(reportingInfractionsState, finalState, data -> true);
        smTemplate.addTransition(reportedInfractionsState, requestingPaymentAuthorizationState, data -> Conditions.isMapBooleanFalse(data, MessageCommonFields.HAS_INFRACTIONS));
        smTemplate.addTransition(requestingPaymentAuthorizationState, reportedPaymentAuthorizationState, data -> Conditions.mapContainsKey(data, MessageCommonFields.AUTHORIZED_PAYMENT));
        smTemplate.addTransition(reportedPaymentAuthorizationState, reportingRejectedPaymentState, data -> Conditions.isMapBooleanFalse(data, MessageCommonFields.AUTHORIZED_PAYMENT));
        smTemplate.addTransition(reportingRejectedPaymentState, finalState, data -> true);
        smTemplate.addTransition(reportedPaymentAuthorizationState, authorizedPaymentState, data -> Conditions.isMapBooleanTrue(data, MessageCommonFields.AUTHORIZED_PAYMENT));
        smTemplate.addTransition(authorizedPaymentState, requestingShippingScheduleState, data -> Conditions.isMapBooleanTrue(data, MessageCommonFields.NEEDS_SHIPPING));
        smTemplate.addTransition(requestingShippingScheduleState, purchaseCompletedState, data -> Conditions.mapContainsKey(data, LocalDefinitions.SHIPPING_SCHEDULE_REQUESTED_FIELD));
        smTemplate.addTransition(authorizedPaymentState, purchaseCompletedState, data -> Conditions.isMapBooleanFalse(data, MessageCommonFields.NEEDS_SHIPPING));


        /* Datos faltante dentro del manejador de request generales */
        ServerStateManager serverStateManager = ServerStateManager.getInstance();
        serverStateManager.setDataProvider(purchasesDataProvider);
        serverStateManager.setStateCollectionName(Definitions.PURCHASES_STATE_COLLECTION_NAME);
        serverStateManager.setIdField(MessageCommonFields.PURCHASE_ID);

        operationProcessor.setPurchaseManager(purchaseManager);
        operationProcessor.setServerStateManager(serverStateManager);

        messagePersistence.setDataProvider(purchasesDataProvider);

        SimulationController simulationController = SimulationController.getInstance();

        simulationController.setMessageProcessor(operationProcessor);
        simulationController.setDataProvider(purchasesDataProvider);
        simulationController.setSmTemplate(smTemplate);
        simulationController.setServerStateManager(serverStateManager);
        simulationController.setSimulationConfiguration(simulationConfiguration);

        simulationController.init();

        messagePersistence.setSimulationController(simulationController);

        controlMessage.setServerStateManager(serverStateManager);
        controlMessage.setSimulationController(simulationController);

        purchaseManager.setDataProvider(commonDataProvider);

        purchaseManager.load();

        communicationHandler.registerReceiver(Definitions.PURCHASES_SERVER_NAME);

        Logging.info("Escuchando mensajes (Ctrl + C para cerrar)...");
    }
}
