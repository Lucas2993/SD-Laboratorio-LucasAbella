package ar.edu.unp.madryn.livremarket.common.threads;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Map;

public class ReceiverWorker extends MessageWorker {

    private String message;

    public ReceiverWorker(String consumerTag, String message) {
        super(consumerTag);
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

        Type dataType = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> data = gson.fromJson(this.message, dataType);

        data.put(MessageCommonFields.MESSAGE_TYPE_ID, type);

        MessageHandler handler = messageHandlerManager.getHandlerForType(messageType);
        if (handler == null) {
            Logging.error("Error: No existe un handler para el tipo de mensaje '" + messageType + "'!");
            return;
        }

        /* Actualizar el reloj propio */
        if(vectorClockController.updateClock()){
            Logging.info("Reloj propio actualizado!");
        }

        /* Actualizar relojes */
        String clockJson = data.remove(CLOCK_FIELD);
        if(!StringUtils.isEmpty(clockJson)){
            Type clocksDataType = new TypeToken<Map<String, Long>>() {
            }.getType();
            Map<String,Long> clocksData = this.gson.fromJson(clockJson, clocksDataType);
            if(!MapUtils.isEmpty(clocksData)){
                vectorClockController.updateClocks(clocksData);
                Logging.info("Relojes actualizados!");
            }
        }

        handler.handle(data);
        Logging.info("Procesamiento finalizado!");
    }
}
