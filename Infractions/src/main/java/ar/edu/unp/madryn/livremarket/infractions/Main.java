package ar.edu.unp.madryn.livremarket.infractions;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.MessageDelivery;
import ar.edu.unp.madryn.livremarket.common.messages.MessageServer;
import ar.edu.unp.madryn.livremarket.common.messages.MessageServerFactory;

public class Main {
    public static void main(String [] args){
        // Configurar la conexion al servidor de mensajes.
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        ConfigurationSection connectionConfiguration = configurationManager.loadConfiguration("connection", ConfigurationSection.CONFIGURATION_FOLDER);

        MessageServerFactory messageServerFactory = MessageServerFactory.getInstance();
        MessageServer messageServer = messageServerFactory.getNewMessageServer(connectionConfiguration);

        // Establecer la conexion al servidor.
        if(!messageServer.connect()){
            System.err.println("No se pudo establecer conexion con el servidor AMQP!");
            return;
        }

        // Escuchar todos los mensajes que vengan con el topico "*.infractions".
        if(messageServer.registerProcessor("*.infractions", new MessageDelivery() {
            @Override
            public void processMessage(String consumerTag, String message) {
                System.out.print("Mensaje recibido: ");
                System.out.print("[" + consumerTag + "] ");
                System.out.println(message);
            }
        })){
            System.out.println("Escuchando mensajes (Ctrl + C para cerrar)...");
        }
        else{
            System.err.println("No se pudo registrar el procesador de mensajes!");
        }
    }
}
