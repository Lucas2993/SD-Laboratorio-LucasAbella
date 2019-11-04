package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.purchases.utils.LocalDefinitions;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class RequestingInfractionsState extends State {

    @Setter
    private CommunicationHandler communicationHandler;

    public RequestingInfractionsState() {
        super("requesting_infractions");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Solicitar informacion sobre infracciones.
         */
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        System.out.println("Solicitando infracciones! (ID = " + purchaseID + ")");

        Map<String,String> messageData = new HashMap<>();

        messageData.put(Definitions.REQUEST_OPERATION_KEY, Operations.GET_INFRACTIONS_OPERATION);
        messageData.put(MessageCommonFields.PURCHASE_ID, purchaseID);

        if(!this.communicationHandler.sendMessage(MessageType.GENERAL, Definitions.INFRACTIONS_SERVER_NAME, messageData)){
            // TODO Error de mensaje que no se pudo enviar
            System.err.println("Error: No se pudo enviar el mensaje al servidor de infracciones!");
            return false;
        }

        data.put(LocalDefinitions.INFRACTIONS_REQUESTED_FIELD, String.valueOf(true));

        return true;
    }
}
