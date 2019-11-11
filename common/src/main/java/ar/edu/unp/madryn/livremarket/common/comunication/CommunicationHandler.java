package ar.edu.unp.madryn.livremarket.common.comunication;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.*;
import ar.edu.unp.madryn.livremarket.common.threads.MessageWorker;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Type;
import java.util.Map;

public class CommunicationHandler {
    private static final String ROUTING_KEY_SEPARATOR = ".";
    private static final String ROUTING_KEY_SEPARATOR_REGEX = "\\.";
    private static final String ROUTING_KEY_ANY_WILDCARD = "*";

    private MessageServer messageServer;

    private static CommunicationHandler instance;

    public static CommunicationHandler getInstance() {
        if (instance == null) {
            instance = new CommunicationHandler();
        }
        return instance;
    }

    private CommunicationHandler() {
    }

    // Mensajes

    public boolean connect() {
        if (this.messageServer != null) {
            // TODO Mensaje de error de conexion ya establecida...
            return false;
        }

        // Configurar la conexion al servidor de mensajes.
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        ConfigurationSection connectionConfiguration = configurationManager.loadConfiguration(Definitions.CONNECTION_CONFIGURATION_FILE, ConfigurationSection.CONFIGURATION_FOLDER);
        if (connectionConfiguration == null) {
            // TODO Mensaje de error sobre que no hay configuracion de conexion...
            return false;
        }

        MessageServerFactory messageServerFactory = MessageServerFactory.getInstance();
        this.messageServer = messageServerFactory.getNewMessageServer(connectionConfiguration);

        // Establecer la conexion al servidor.
        if (!messageServer.connect()) {
            this.messageServer = null;
            System.err.println("No se pudo establecer conexion con el servidor AMQP!");
            return false;
        }
        return true;
    }

    public boolean sendMessage(MessageType type, String serverName, Map<String, String> data) {
        // TODO Comprobar no haber recibido parametros invalidos...
        String routingKey = type.topic() + ROUTING_KEY_SEPARATOR + serverName;

        Gson gson = new Gson();
        String message = gson.toJson(data);

        System.out.println("Mensaje enviado! con el topico '" + routingKey + "' (Contenido = " + message + ")");

        // TODO Comprobar que el messageServer este creado...
        return this.messageServer.sendMessage(routingKey, message);
    }

    public void registerReceiver(String serverName) {
        // TODO Comprobar no haber recibido parametros invalidos...
        String bindingKey = ROUTING_KEY_ANY_WILDCARD + ROUTING_KEY_SEPARATOR + serverName;

        this.messageServer.registerProcessor(bindingKey, (consumerTag, message) -> {
            MessageWorker messageWorker = new MessageWorker(consumerTag, message);

            messageWorker.start();
        });
    }
}
