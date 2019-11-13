package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class RequestingDeliveryCostState extends State {
    @Setter
    private CommunicationHandler communicationHandler;

    public RequestingDeliveryCostState() {
        super("requesting_delivery_cost");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Enviar mensaje al servidor de envios
         */
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        Logging.info("Solicitando el costo de envio! (ID = " + purchaseID + ")");

        Map<String,String> messageData = new HashMap<>();

        messageData.put(Definitions.REQUEST_OPERATION_KEY, Operations.GET_DELIVERY_COST_OPERATION);
        messageData.put(MessageCommonFields.PURCHASE_ID, purchaseID);

        if(!this.communicationHandler.sendMessage(MessageType.GENERAL, Definitions.DELIVERIES_SERVER_NAME, messageData)){
            // TODO Error de mensaje que no se pudo enviar
            Logging.error("Error: No se pudo enviar el mensaje al servidor de envios!");
            return false;
        }

        return true;
    }
}
