package ar.edu.unp.madryn.livremarket.common.comunication;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.*;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CommunicationHandler {
    private static final String ROUTING_KEY_SEPARATOR = ".";
    private static final String ROUTING_KEY_SEPARATOR_REGEX = "\\.";
    private static final String ROUTING_KEY_ANY_WILDCARD = "*";

    private MessageServer messageServer;

    private Map<MessageType, MessageHandler> handlers;

    private static CommunicationHandler instance;

    public static CommunicationHandler getInstance() {
        if (instance == null) {
            instance = new CommunicationHandler();
        }
        return instance;
    }

    private CommunicationHandler() {
        this.handlers = new HashMap<>();
    }

    // Handlers

    public boolean registerHandler(MessageHandler handler, MessageType... types) {
        for(MessageType type : types) {
            if (this.handlers.containsKey(type)) {
                continue;
            }

            this.handlers.put(type, handler);
        }

        return true;
    }

    private MessageHandler getHandlerForType(MessageType type) {
        return this.handlers.get(type);
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
            System.out.println("Mensaje recibido! con el topico '" + consumerTag + "' (Contenido = " + message + ")");

            String[] tags = consumerTag.split(ROUTING_KEY_SEPARATOR_REGEX);
            if (ArrayUtils.isEmpty(tags)) {
                System.err.println("Error: El mensaje no tiene topicos!");
                return;
            }

            String type = tags[0];
            MessageType messageType = MessageType.fromTopic(type);
            if (messageType == null) {
                System.err.println("Error: Tipo de mensaje no reconocido!");
                return;
            }

            Gson gson = new Gson();
            Type dataType = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> data = gson.fromJson(message, dataType);

            data.put(MessageCommonFields.MESSAGE_TYPE_ID, type);

            MessageHandler handler = getHandlerForType(messageType);
            if (handler == null) {
                System.err.println("Error: No existe un handler para el tipo de mensaje '" + messageType + "'!");
                return;
            }

            handler.handle(data);
        });
    }
}
