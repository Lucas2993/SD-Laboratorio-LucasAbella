package ar.edu.unp.madryn.livremarket.common.comunication;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageServer;
import ar.edu.unp.madryn.livremarket.common.messages.MessageServerFactory;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
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

    public boolean registerHandler(MessageType type, MessageHandler handler) {
        if (this.handlers.containsKey(type)) {
            return false;
        }

        this.handlers.put(type, handler);
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

        // TODO Comprobar que el messageServer este creado...
        return this.messageServer.sendMessage(routingKey, message);
    }

    public void registerReceiver(String serverName) {
        // TODO Comprobar no haber recibido parametros invalidos...
        String bindingKey = ROUTING_KEY_ANY_WILDCARD + ROUTING_KEY_SEPARATOR + serverName;

        this.messageServer.registerProcessor(bindingKey, (consumerTag, message) -> {
            String[] tags = consumerTag.split(ROUTING_KEY_SEPARATOR_REGEX);
            if (ArrayUtils.isEmpty(tags)) {
                return;
            }

            String type = tags[0];
            MessageType messageType = MessageType.fromTopic(type);
            if (messageType == null) {
                return;
            }

            Gson gson = new Gson();
            Type dataType = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> data = gson.fromJson(message, dataType);

            MessageHandler handler = getHandlerForType(messageType);
            if (handler == null) {
                // TODO Mensaje de error
                return;
            }

            handler.handle(data);
        });
    }
}
