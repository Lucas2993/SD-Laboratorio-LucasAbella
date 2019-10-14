package ar.edu.unp.madryn.livremarket.deliveries;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.deliveries.messages.GeneralRequest;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class Main {
    public static void main(String [] args){
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        ConfigurationSection simulationConfiguration = configurationManager.loadConfiguration("simulation", ConfigurationSection.CONFIGURATION_FOLDER);
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

        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase(Definitions.DELIVERIES_SERVER_NAME);

        generalRequest.setDatabase(database);

        communicationHandler.registerReceiver(Definitions.DELIVERIES_SERVER_NAME);

        System.out.println("Escuchando mensajes (Ctrl + C para cerrar)...");
    }
}
