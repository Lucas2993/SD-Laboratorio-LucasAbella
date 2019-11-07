package ar.edu.unp.madryn.livremarket.deliveries.sm;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.Results;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.deliveries.utils.LocalDefinitions;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class ReportingCostState extends State {

    @Setter
    private CommunicationHandler communicationHandler;

    public ReportingCostState() {
        super("reporting_cost");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        String costString = data.get(MessageCommonFields.DELIVERY_COST);

        System.out.println("Informando costo de envio (ID =" + purchaseID + ")");

        Map<String, String> responseData = new HashMap<>();

        responseData.put(Definitions.INFORMATION_REFERENCE_KEY, Results.DELIVERY_COST_REFERENCE_ID);
        responseData.put(MessageCommonFields.DELIVERY_COST, costString);
        responseData.put(MessageCommonFields.PURCHASE_ID, purchaseID);

        communicationHandler.sendMessage(MessageType.RESULT, Definitions.PURCHASES_SERVER_NAME, responseData);

        return true;
    }
}
