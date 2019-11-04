package ar.edu.unp.madryn.livremarket.products;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.db.DataProviderFactory;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.sm.*;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.common.data.ProductManager;
import ar.edu.unp.madryn.livremarket.products.messages.GeneralRequest;
import ar.edu.unp.madryn.livremarket.products.messages.ResultInformation;
import ar.edu.unp.madryn.livremarket.products.sm.*;
import ar.edu.unp.madryn.livremarket.products.utils.LocalDefinitions;
import org.apache.commons.collections4.MapUtils;

public class Main {
    public static void main(String[] args) {
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

        ResultInformation resultInformation = new ResultInformation();

        communicationHandler.registerHandler(MessageType.RESULT, resultInformation);

        GeneralRequest generalRequest = new GeneralRequest();

        generalRequest.setCommunicationHandler(communicationHandler);
        generalRequest.setSimulationConfiguration(simulationConfiguration);

        communicationHandler.registerHandler(MessageType.GENERAL, generalRequest);

        if (!communicationHandler.connect()) {
            System.err.println("No se pudo establecer conexion con el servidor AMQP!");
            return;
        }

        DataProviderFactory dataProviderFactory = DataProviderFactory.getInstance();

        DataProvider commonDataProvider = dataProviderFactory.getProviderInstance(connectionConfiguration, Definitions.COMMON_DATABASE_NAME);
        DataProvider productsDataProvider = dataProviderFactory.getProviderInstance(connectionConfiguration, Definitions.PRODUCTS_SERVER_NAME);

        if(!commonDataProvider.connect() || !productsDataProvider.connect()){
            System.err.println("No se pudo establecer conexion con el servidor de base de datos!");
            return;
        }

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
        smTemplate.addTransition(initialState, reservedProductState, data -> data.containsKey(MessageCommonFields.PURCHASE_ID) && data.containsKey(MessageCommonFields.PRODUCT_ID));
        smTemplate.addTransition(reservedProductState, reportedInfractionsState, data -> data.containsKey(MessageCommonFields.HAS_INFRACTIONS));
        smTemplate.addTransition(reportedInfractionsState, reportedPaymentState, data -> data.containsKey(MessageCommonFields.AUTHORIZED_PAYMENT));
        smTemplate.addTransition(reportedPaymentState, releasingProductState, data -> MapUtils.getBoolean(data, MessageCommonFields.HAS_INFRACTIONS));
        smTemplate.addTransition(reportedPaymentState, noInfractionsState, data -> !MapUtils.getBoolean(data, MessageCommonFields.HAS_INFRACTIONS));
        smTemplate.addTransition(noInfractionsState, releasingProductState, data -> !MapUtils.getBoolean(data, MessageCommonFields.AUTHORIZED_PAYMENT));
        smTemplate.addTransition(noInfractionsState, authorizedPaymentState, data -> MapUtils.getBoolean(data, MessageCommonFields.AUTHORIZED_PAYMENT));
        smTemplate.addTransition(releasingProductState, finalState, data -> !MapUtils.getBoolean(data, LocalDefinitions.RESERVED_PRODUCT_FIELD));
        smTemplate.addTransition(authorizedPaymentState, waitingBookedShipmentState, data -> MapUtils.getBoolean(data, MessageCommonFields.NEEDS_SHIPPING));
        smTemplate.addTransition(waitingBookedShipmentState, sendingProductState, data -> MapUtils.getBoolean(data, MessageCommonFields.BOOKED_SHIPPING));
        smTemplate.addTransition(authorizedPaymentState, finalState, data -> !MapUtils.getBoolean(data, MessageCommonFields.NEEDS_SHIPPING));
        smTemplate.addTransition(sendingProductState, finalState, data -> data.containsKey(LocalDefinitions.PRODUCT_SENT_FIELD));

        /* Datos faltante dentro del manejador de request generales */
        generalRequest.setSmTemplate(smTemplate);
        generalRequest.setStateDataProvider(productsDataProvider);

        resultInformation.setSmTemplate(smTemplate);
        resultInformation.setStateDataProvider(productsDataProvider);

        productManager.setDataProvider(commonDataProvider);

        productManager.load();

        communicationHandler.registerReceiver(Definitions.PRODUCTS_SERVER_NAME);

        System.out.println("Escuchando mensajes (Ctrl + C para cerrar)...");
    }
}
