package ar.edu.unp.madryn.livremarket.deliveries.sm;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.Results;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import ar.edu.unp.madryn.livremarket.deliveries.utils.LocalDefinitions;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class ReportingBookedDeliveryState extends State {
    @Setter
    private CommunicationHandler communicationHandler;

    public ReportingBookedDeliveryState() {
        super("reporting_booked_delivery");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        Logging.info("Informando envio agendado! (ID = " + purchaseID + ")");

        Map<String, String> resultData = new HashMap<>();

        resultData.put(Definitions.INFORMATION_REFERENCE_KEY, Results.BOOK_SHIPMENT_REFERENCE_ID);
        resultData.put(MessageCommonFields.PURCHASE_ID, purchaseID);

        communicationHandler.sendMessage(MessageType.RESULT, Definitions.PRODUCTS_SERVER_NAME, resultData);

        Logging.info("Envio agendado informado! (ID = " + purchaseID + ")");

        data.put(LocalDefinitions.REPORTED_BOOKED_DELIVERY_FIELD, String.valueOf(true));

        return true;
    }
}
