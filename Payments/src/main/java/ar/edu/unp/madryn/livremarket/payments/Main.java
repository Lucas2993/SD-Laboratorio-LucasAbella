package ar.edu.unp.madryn.livremarket.payments;

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
import ar.edu.unp.madryn.livremarket.payments.simulation.OperationProcessor;
import ar.edu.unp.madryn.livremarket.payments.sm.reportingPaymentState;
import ar.edu.unp.madryn.livremarket.payments.sm.ResolvingPaymentState;
import ar.edu.unp.madryn.livremarket.payments.utils.LocalDefinitions;
import org.apache.commons.collections4.MapUtils;

public class Main {
    public static void main(String [] args){
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

        DataProvider paymentsDataProvider = dataProviderFactory.getProviderInstance(connectionConfiguration, Definitions.PAYMENTS_SERVER_NAME);

        if(!paymentsDataProvider.connect()){
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

        ResolvingPaymentState resolvingPaymentState = new ResolvingPaymentState();
        resolvingPaymentState.setSimulationConfiguration(simulationConfiguration);
        smTemplate.addState(resolvingPaymentState);

        reportingPaymentState reportingPaymentState = new reportingPaymentState();
        reportingPaymentState.setCommunicationHandler(communicationHandler);
        smTemplate.addState(reportingPaymentState);

        /* Transiciones */
        smTemplate.addTransition(initialState, resolvingPaymentState, data -> MapUtils.getBoolean(data, LocalDefinitions.REQUESTED_PAYMENT_FIELD));
        smTemplate.addTransition(resolvingPaymentState, reportingPaymentState, data -> data.containsKey(MessageCommonFields.AUTHORIZED_PAYMENT));
        smTemplate.addTransition(reportingPaymentState, finalState, data -> MapUtils.getBoolean(data, LocalDefinitions.REPORTED_PAYMENT_FIELD));

        /* Datos faltante dentro del manejador de request generales */
        messagePersistence.setDataProvider(paymentsDataProvider);

        operationProcessor.setStateDataProvider(paymentsDataProvider);

        SimulationController simulationController = SimulationController.getInstance();

        simulationController.setMessageProcessor(operationProcessor);
        simulationController.setDataProvider(paymentsDataProvider);
        simulationController.setSmTemplate(smTemplate);
        simulationController.setStateCollectionName(Definitions.PAYMENTS_STATE_COLLECTION_NAME);

        communicationHandler.registerReceiver(Definitions.PAYMENTS_SERVER_NAME);

        System.out.println("Escuchando mensajes (Ctrl + C para cerrar)...");
    }
}
