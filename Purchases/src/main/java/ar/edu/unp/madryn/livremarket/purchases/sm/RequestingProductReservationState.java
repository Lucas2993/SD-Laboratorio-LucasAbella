package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestingProductReservationState extends State {

    @Setter
    private CommunicationHandler communicationHandler;

    public RequestingProductReservationState() {
        super("requesting_product_reservation");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Enviar solicitud de reservacion de producto.
         */
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        System.out.println("Solicitando la reserva del producto! (ID = " + purchaseID + ")");

        String productID = data.get(MessageCommonFields.PRODUCT_ID);
        int amount = MapUtils.getIntValue(data, MessageCommonFields.PRODUCT_AMOUNT, 1);

        Map<String,String> messageData = new HashMap<>();

        messageData.put(Definitions.REQUEST_OPERATION_KEY, Operations.PRODUCT_RESERVATION_OPERATION);
        messageData.put(MessageCommonFields.PURCHASE_ID, purchaseID);
        messageData.put(MessageCommonFields.PRODUCT_ID, productID);
        messageData.put(MessageCommonFields.PRODUCT_AMOUNT, String.valueOf(amount));

        if(!this.communicationHandler.sendMessage(MessageType.GENERAL, Definitions.PRODUCTS_SERVER_NAME, messageData)){
            // TODO Error de mensaje que no se pudo enviar
            System.err.println("Error: No se pudo enviar el mensaje al servidor de productos!");
            return false;
        }

        return true;
    }
}
