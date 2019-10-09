package ar.edu.unp.madryn.livremarket.products;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.products.messages.ResultInformation;

public class Main {
    public static void main(String[] args) {
        CommunicationHandler communicationHandler = CommunicationHandler.getInstance();

        ResultInformation resultInformation = new ResultInformation();

        communicationHandler.registerHandler(MessageType.RESULT, resultInformation);

        if (!communicationHandler.connect()) {
            System.err.println("No se pudo establecer conexion con el servidor AMQP!");
            return;
        }

        communicationHandler.registerReceiver(Definitions.PRODUCTS_SERVER_NAME);

        System.out.println("Escuchando mensajes (Ctrl + C para cerrar)...");
    }
}
