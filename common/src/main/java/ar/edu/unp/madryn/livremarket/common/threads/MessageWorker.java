package ar.edu.unp.madryn.livremarket.common.threads;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandlerManager;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Type;
import java.util.Map;

public class MessageWorker extends Thread {
    private static final String ROUTING_KEY_SEPARATOR_REGEX = "\\.";

    private String consumerTag;

    private String message;

    @Setter
    private static MessageHandlerManager messageHandlerManager;

    public MessageWorker(String consumerTag, String message) {
        this.consumerTag = consumerTag;
        this.message = message;
    }

    @Override
    public void run() {
        Logging.info("Mensaje recibido! con el topico '" + consumerTag + "' (Contenido = " + message + ")");

        String[] tags = this.consumerTag.split(ROUTING_KEY_SEPARATOR_REGEX);
        if (ArrayUtils.isEmpty(tags)) {
            Logging.error("Error: El mensaje no tiene topicos!");
            return;
        }

        String type = tags[0];
        MessageType messageType = MessageType.fromTopic(type);
        if (messageType == null) {
            Logging.error("Error: Tipo de mensaje no reconocido!");
            return;
        }

        Gson gson = new Gson();
        Type dataType = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> data = gson.fromJson(this.message, dataType);

        data.put(MessageCommonFields.MESSAGE_TYPE_ID, type);

        MessageHandler handler = messageHandlerManager.getHandlerForType(messageType);
        if (handler == null) {
            Logging.error("Error: No existe un handler para el tipo de mensaje '" + messageType + "'!");
            return;
        }

        handler.handle(data);
        Logging.info("Procesamiento finalizado!");
    }
}
