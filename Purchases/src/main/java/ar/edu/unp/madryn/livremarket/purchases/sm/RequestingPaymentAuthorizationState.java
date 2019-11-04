package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class RequestingPaymentAuthorizationState extends State {

    @Setter
    private CommunicationHandler communicationHandler;

    public RequestingPaymentAuthorizationState() {
        super("requesting_payment_authorization");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        System.out.println("Solicitando autorizacion de pago! (ID = " + purchaseID + ")");

        Map<String,String> messageData = new HashMap<>();

        messageData.put(Definitions.REQUEST_OPERATION_KEY, Operations.AUTHORIZE_PAYMENT_OPERATION);
        messageData.put(MessageCommonFields.PURCHASE_ID, purchaseID);

        if(!this.communicationHandler.sendMessage(MessageType.GENERAL, Definitions.PAYMENTS_SERVER_NAME, messageData)){
            // TODO Error de mensaje que no se pudo enviar
            System.err.println("Error: No se pudo enviar el mensaje al servidor de infracciones!");
            return false;
        }

        return true;
    }
}
