package ar.edu.unp.madryn.livremarket.deliveries;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
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
import ar.edu.unp.madryn.livremarket.deliveries.simulation.OperationProcessor;
import ar.edu.unp.madryn.livremarket.deliveries.sm.BookingDeliveryState;
import ar.edu.unp.madryn.livremarket.deliveries.sm.CalculatingCostState;
import ar.edu.unp.madryn.livremarket.deliveries.sm.ReportingBookedDeliveryState;
import ar.edu.unp.madryn.livremarket.deliveries.sm.ReportingCostState;
import ar.edu.unp.madryn.livremarket.deliveries.utils.LocalDefinitions;
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

        if (!communicationHandler.connect()) {
            System.err.println("No se pudo establecer conexion con el servidor AMQP!");
            return;
        }

        DataProviderFactory dataProviderFactory = DataProviderFactory.getInstance();

        DataProvider deliveriesDataProvider = dataProviderFactory.getProviderInstance(connectionConfiguration, Definitions.DELIVERIES_SERVER_NAME);

        if (!deliveriesDataProvider.connect()) {
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

        CalculatingCostState calculatingCostState = new CalculatingCostState();
        calculatingCostState.setSimulationConfiguration(simulationConfiguration);
        smTemplate.addState(calculatingCostState);

        ReportingCostState reportingCostState = new ReportingCostState();
        reportingCostState.setCommunicationHandler(communicationHandler);
        smTemplate.addState(reportingCostState);

        BookingDeliveryState bookingDeliveryState = new BookingDeliveryState();
        bookingDeliveryState.setDataProvider(deliveriesDataProvider);
        smTemplate.addState(bookingDeliveryState);

        ReportingBookedDeliveryState reportingBookedDeliveryState = new ReportingBookedDeliveryState();
        reportingBookedDeliveryState.setCommunicationHandler(communicationHandler);
        smTemplate.addState(reportingBookedDeliveryState);

        /* Transiciones */
        smTemplate.addTransition(initialState, calculatingCostState, data -> MapUtils.getBoolean(data, LocalDefinitions.REQUESTED_COST_FIELD));
        smTemplate.addTransition(calculatingCostState, reportingCostState, data -> data.containsKey(MessageCommonFields.DELIVERY_COST));
        smTemplate.addTransition(reportingCostState, bookingDeliveryState, data -> MapUtils.getBoolean(data, LocalDefinitions.REPORTED_BOOKED_DELIVERY_FIELD));
        smTemplate.addTransition(bookingDeliveryState, reportingBookedDeliveryState, data -> MapUtils.getBoolean(data, LocalDefinitions.BOOKED_DELIVERY_FIELD));
        smTemplate.addTransition(reportingBookedDeliveryState, finalState, data -> MapUtils.getBoolean(data, LocalDefinitions.REPORTED_BOOKED_DELIVERY_FIELD));


        /* Datos faltante dentro del manejador de request generales */
        messagePersistence.setDataProvider(deliveriesDataProvider);

        operationProcessor.setStateDataProvider(deliveriesDataProvider);

        SimulationController simulationController = SimulationController.getInstance();

        simulationController.setMessageProcessor(operationProcessor);
        simulationController.setDataProvider(deliveriesDataProvider);
        simulationController.setSmTemplate(smTemplate);
        simulationController.setStateCollectionName(Definitions.DELIVERIES_STATE_COLLECTION_NAME);

        communicationHandler.registerReceiver(Definitions.DELIVERIES_SERVER_NAME);

        System.out.println("Escuchando mensajes (Ctrl + C para cerrar)...");
    }
}
