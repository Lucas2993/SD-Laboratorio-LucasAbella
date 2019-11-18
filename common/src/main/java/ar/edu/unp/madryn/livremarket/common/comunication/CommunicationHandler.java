package ar.edu.unp.madryn.livremarket.common.comunication;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageServer;
import ar.edu.unp.madryn.livremarket.common.messages.MessageServerFactory;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.threads.MessageWorker;
import ar.edu.unp.madryn.livremarket.common.threads.ReceiverWorker;
import ar.edu.unp.madryn.livremarket.common.threads.SenderWorker;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import lombok.Setter;

import java.util.Map;

public class CommunicationHandler {
    private static final String ROUTING_KEY_SEPARATOR = ".";
    private static final String ROUTING_KEY_ANY_WILDCARD = "*";

    @Setter
    private String serverID;
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
            Logging.error("No se pudo establecer conexion con el servidor AMQP!");
            return false;
        }
        return true;
    }

    public boolean sendMessage(MessageType type, String serverName, Map<String, String> data) {
        // TODO Comprobar no haber recibido parametros invalidos...
        String routingKey = type.topic() + ROUTING_KEY_SEPARATOR + serverName;

        /* Agregado de servidor de origen */
        data.put(MessageCommonFields.SOURCE_SERVER, this.serverID);

        MessageWorker messageWorker = new SenderWorker(routingKey, data, this.messageServer);

        messageWorker.start();

        return true;
    }

    public void registerReceiver(String serverName) {
        // TODO Comprobar no haber recibido parametros invalidos...
        String bindingKey = ROUTING_KEY_ANY_WILDCARD + ROUTING_KEY_SEPARATOR + serverName;

        this.messageServer.registerProcessor(bindingKey, (consumerTag, message) -> {
            MessageWorker messageWorker = new ReceiverWorker(consumerTag, message);

            messageWorker.start();
        });
    }
}
