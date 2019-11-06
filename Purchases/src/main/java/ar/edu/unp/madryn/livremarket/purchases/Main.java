package ar.edu.unp.madryn.livremarket.purchases;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.data.PurchaseManager;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.db.DataProviderFactory;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.types.MessagePersistence;
import ar.edu.unp.madryn.livremarket.common.simulation.SimulationController;
import ar.edu.unp.madryn.livremarket.common.sm.FinalState;
import ar.edu.unp.madryn.livremarket.common.sm.InitialState;
import ar.edu.unp.madryn.livremarket.common.sm.Template;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.purchases.simulation.OperationProcessor;
import ar.edu.unp.madryn.livremarket.purchases.sm.*;
import ar.edu.unp.madryn.livremarket.purchases.utils.LocalDefinitions;
import org.apache.commons.collections4.MapUtils;

public class Main {
    public static void main(String [] args){
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        ConfigurationSection connectionConfiguration = configurationManager.loadConfiguration(Definitions.CONNECTION_CONFIGURATION_FILE, ConfigurationSection.CONFIGURATION_FOLDER);
        if (connectionConfiguration == null) {
            System.err.println("Error: La configuracion de la conexion a la base de datos no existe!");
            return;
        }

        ConfigurationSection simulationConfiguration = configurationManager.loadConfiguration(Definitions.SIMULATION_CONFIGURATION_FILE, ConfigurationSection.CONFIGURATION_FOLDER);
        if (simulationConfiguration == null) {
            System.err.println("Error: La configuracion de la simulacion no existe!");
            return;
        }

        CommunicationHandler communicationHandler = CommunicationHandler.getInstance();

        OperationProcessor operationProcessor = new OperationProcessor();

        MessagePersistence messagePersistence = new MessagePersistence();

        communicationHandler.registerHandler(messagePersistence, MessageType.GENERAL, MessageType.RESULT);

        if (!communicationHandler.connect()) {
            System.err.println("No se pudo establecer conexion con el servidor AMQP!");
            return;
        }

        DataProviderFactory dataProviderFactory = DataProviderFactory.getInstance();

        DataProvider commonDataProvider = dataProviderFactory.getProviderInstance(connectionConfiguration, Definitions.COMMON_DATABASE_NAME);
        DataProvider purchasesDataProvider = dataProviderFactory.getProviderInstance(connectionConfiguration, Definitions.PURCHASES_SERVER_NAME);

        if(!commonDataProvider.connect() || !purchasesDataProvider.connect()){
            System.err.println("No se pudo establecer conexion con el servidor de base de datos!");
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
        smTemplate.addState(selectingDeliveryState);

        RequestingDeliveryCostState requestingDeliveryCostState = new RequestingDeliveryCostState();
        requestingDeliveryCostState.setCommunicationHandler(communicationHandler);
        smTemplate.addState(requestingDeliveryCostState);

        SelectingPaymentState selectingPaymentState = new SelectingPaymentState();
        selectingPaymentState.setPurchaseManager(purchaseManager);
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
        smTemplate.addTransition(initialState, selectingProductState, data -> data.containsKey(MessageCommonFields.CLIENT_ID));
        smTemplate.addTransition(selectingProductState, requestingProductReservationState, data -> data.containsKey(MessageCommonFields.PRODUCT_ID) && data.containsKey(MessageCommonFields.PRODUCT_AMOUNT));
        smTemplate.addTransition(requestingProductReservationState, requestingInfractionsState, data -> MapUtils.getBoolean(data, LocalDefinitions.PRODUCT_RESERVATION_REQUESTED_FIELD));
        smTemplate.addTransition(requestingInfractionsState, selectingDeliveryState, data -> MapUtils.getBoolean(data, LocalDefinitions.INFRACTIONS_REQUESTED_FIELD));
        smTemplate.addTransition(selectingDeliveryState, requestingDeliveryCostState, data -> MapUtils.getBoolean(data, MessageCommonFields.NEEDS_SHIPPING));
        smTemplate.addTransition(selectingDeliveryState, selectingPaymentState, data -> !MapUtils.getBoolean(data, MessageCommonFields.NEEDS_SHIPPING));
        smTemplate.addTransition(requestingDeliveryCostState, selectingPaymentState, data -> data.containsKey(MessageCommonFields.DELIVERY_COST));
        smTemplate.addTransition(selectingPaymentState, purchaseConfirmedState, data -> data.containsKey(MessageCommonFields.PAYMENT_METHOD));
        smTemplate.addTransition(purchaseConfirmedState, reportedInfractionsState, data -> data.containsKey(MessageCommonFields.HAS_INFRACTIONS));
        smTemplate.addTransition(reportedInfractionsState, reportingInfractionsState, data -> MapUtils.getBoolean(data, MessageCommonFields.HAS_INFRACTIONS));
        smTemplate.addTransition(reportingInfractionsState, finalState, data -> true);
        smTemplate.addTransition(reportedInfractionsState, requestingPaymentAuthorizationState, data -> !MapUtils.getBoolean(data, MessageCommonFields.HAS_INFRACTIONS));
        smTemplate.addTransition(requestingPaymentAuthorizationState, reportedPaymentAuthorizationState, data -> data.containsKey(MessageCommonFields.AUTHORIZED_PAYMENT));
        smTemplate.addTransition(reportedPaymentAuthorizationState, reportingRejectedPaymentState, data -> !MapUtils.getBoolean(data, MessageCommonFields.AUTHORIZED_PAYMENT));
        smTemplate.addTransition(reportingRejectedPaymentState, finalState, data -> true);
        smTemplate.addTransition(reportedPaymentAuthorizationState, authorizedPaymentState, data -> MapUtils.getBoolean(data, MessageCommonFields.AUTHORIZED_PAYMENT));
        smTemplate.addTransition(authorizedPaymentState, requestingShippingScheduleState, data -> MapUtils.getBoolean(data, MessageCommonFields.NEEDS_SHIPPING));
        smTemplate.addTransition(requestingShippingScheduleState, purchaseCompletedState, data -> data.containsKey(LocalDefinitions.SHIPPING_SCHEDULE_REQUESTED_FIELD));
        smTemplate.addTransition(authorizedPaymentState, purchaseCompletedState, data -> !MapUtils.getBoolean(data, MessageCommonFields.NEEDS_SHIPPING));



        /* Datos faltante dentro del manejador de request generales */
        operationProcessor.setPurchaseManager(purchaseManager);
        operationProcessor.setStateDataProvider(purchasesDataProvider);

        messagePersistence.setDataProvider(purchasesDataProvider);

        SimulationController simulationController = SimulationController.getInstance();

        simulationController.setMessageProcessor(operationProcessor);
        simulationController.setDataProvider(purchasesDataProvider);
        simulationController.setSmTemplate(smTemplate);
        simulationController.setStateCollectionName(Definitions.PURCHASES_STATE_COLLECTION_NAME);

        purchaseManager.setDataProvider(commonDataProvider);

        purchaseManager.load();

        communicationHandler.registerReceiver(Definitions.PURCHASES_SERVER_NAME);

        System.out.println("Escuchando mensajes (Ctrl + C para cerrar)...");
    }
}
