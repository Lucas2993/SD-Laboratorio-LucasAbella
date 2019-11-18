package ar.edu.unp.madryn.livremarket.common.threads;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageServer;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;

import java.util.Map;

public class SenderWorker extends MessageWorker {

    private Map<String,String> data;

    private MessageServer messageServer;

    public SenderWorker(String consumerTag, Map<String, String> data, MessageServer messageServer) {
        super(consumerTag);
        this.data = data;
        this.messageServer = messageServer;
    }

    @Override
    public void run() {
        /* Actualizar el reloj propio */
        if(vectorClockController.updateClock()){
            Logging.info("Reloj propio actualizado!");
        }

        /* Agregado de servidor de origen */
        data.put(MessageCommonFields.SOURCE_SERVER, serverID);
        /* Agregado de reloj */
        data.put(CLOCK_FIELD, this.gson.toJson(vectorClockController.getClocks()));

        String message = this.gson.toJson(data);

        Logging.info("Enviando mensaje con el topico '" + this.consumerTag + "' (Contenido = " + message + ")!");

        this.messageServer.sendMessage(this.consumerTag, message);

    }
}
