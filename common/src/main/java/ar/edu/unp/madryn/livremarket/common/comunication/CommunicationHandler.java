package ar.edu.unp.madryn.livremarket.common.comunication;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Type;
import java.util.Map;

public class CommunicationHandler {
    private static final String ROUTING_KEY_SEPARATOR = ".";
    private static final String ROUTING_KEY_ANY_WILDCARD = "*";

    private MessageServer messageServer;

    private static CommunicationHandler instance;

    public static CommunicationHandler getInstance() {
        if(instance == null){
            instance = new CommunicationHandler();
        }
        return instance;
    }

    private CommunicationHandler(){

    }

    public boolean connect(){
        if(this.messageServer != null){
            // TODO Mensaje de error de conexion ya establecida...
            return false;
        }

        // Configurar la conexion al servidor de mensajes.
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        ConfigurationSection connectionConfiguration = configurationManager.loadConfiguration("connection", ConfigurationSection.CONFIGURATION_FOLDER);
        if(connectionConfiguration == null){
            // TODO Mensaje de error sobre que no hay configuracion de conexion...
            return false;
        }

        MessageServerFactory messageServerFactory = MessageServerFactory.getInstance();
        this.messageServer = messageServerFactory.getNewMessageServer(connectionConfiguration);

        // Establecer la conexion al servidor.
        if(!messageServer.connect()){
            this.messageServer = null;
            System.err.println("No se pudo establecer conexion con el servidor AMQP!");
            return false;
        }
        return true;
    }

    public boolean sendMessage(MessageType type, String serverName, Map<String,String> data){
        // TODO Comprobar no haber recibido parametros invalidos...
        String routingKey = type.topic() + ROUTING_KEY_SEPARATOR + serverName;

        Gson gson = new Gson();
        String message = gson.toJson(data);

        // TODO Comprobar que el messageServer este creado...
        return this.messageServer.sendMessage(routingKey, message);
    }

    public void registerHandler(String serverName, MessageHandler handler){
        // TODO Comprobar no haber recibido parametros invalidos...
        String bindingKey = ROUTING_KEY_ANY_WILDCARD + ROUTING_KEY_SEPARATOR + serverName;

        this.messageServer.registerProcessor(bindingKey, new MessageDelivery() {
            @Override
            public void processMessage(String consumerTag, String message) {
                String [] tags = consumerTag.split(ROUTING_KEY_SEPARATOR);
                if(ArrayUtils.isEmpty(tags)){
                    return;
                }

                String type = tags[0];
                MessageType messageType = MessageType.fromTopic(type);
                if(messageType == null){
                    return;
                }

                Gson gson = new Gson();
                Type dataType = new TypeToken<Map<String, String>>(){}.getType();
                Map<String,String> data = gson.fromJson(message, dataType);

                switch (messageType){
                    case CONTROL:
                        handler.processControl(data);
                        break;
                    case MONITOR:
                        handler.processMonitor(data);
                        break;
                    case REQUEST:
                        handler.processRequest(data);
                        break;
                    case INFORMATION:
                        handler.processInformation(data);
                        break;
                }
            }
        });
    }
}
