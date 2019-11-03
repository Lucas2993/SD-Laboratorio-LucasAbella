package ar.edu.unp.madryn.livremarket.purchases;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.data.PurchaseManager;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.db.DataProviderFactory;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.sm.FinalState;
import ar.edu.unp.madryn.livremarket.common.sm.InitialState;
import ar.edu.unp.madryn.livremarket.common.sm.Template;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.purchases.messages.GeneralRequest;

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


        /* Transiciones */



        /* Datos faltante dentro del manejador de request generales */
        generalRequest.setSmTemplate(smTemplate);
        generalRequest.setStateDataProvider(purchasesDataProvider);
        generalRequest.setPurchaseManager(purchaseManager);

        purchaseManager.setDataProvider(commonDataProvider);

        purchaseManager.load();

        communicationHandler.registerReceiver(Definitions.PURCHASES_SERVER_NAME);

        System.out.println("Escuchando mensajes (Ctrl + C para cerrar)...");
    }
}
