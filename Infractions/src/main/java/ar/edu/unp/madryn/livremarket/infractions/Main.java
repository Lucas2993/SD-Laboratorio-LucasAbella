package ar.edu.unp.madryn.livremarket.infractions;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.infractions.messages.GeneralRequest;

public class Main {
    public static void main(String[] args) {
        CommunicationHandler communicationHandler = CommunicationHandler.getInstance();

        GeneralRequest generalRequest = new GeneralRequest();

        communicationHandler.registerHandler(MessageType.GENERAL, generalRequest);

        if (!communicationHandler.connect()) {
            System.err.println("No se pudo establecer conexion con el servidor AMQP!");
            return;
        }

        communicationHandler.registerReceiver("infractions");

        System.out.println("Escuchando mensajes (Ctrl + C para cerrar)...");
    }
}
