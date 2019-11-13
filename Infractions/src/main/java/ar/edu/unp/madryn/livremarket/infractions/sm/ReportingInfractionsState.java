package ar.edu.unp.madryn.livremarket.infractions.sm;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.Results;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import ar.edu.unp.madryn.livremarket.infractions.utils.LocalDefinitions;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

public class ReportingInfractionsState extends State {

    @Setter
    private CommunicationHandler communicationHandler;

    public ReportingInfractionsState() {
        super("reporting_infractions");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        boolean hasInfraction = MapUtils.getBoolean(data, MessageCommonFields.HAS_INFRACTIONS, false);

        Logging.info("Informando infracciones (ID =" + purchaseID + ")");

        Map<String, String> responseData = new HashMap<>();

        responseData.put(Definitions.INFORMATION_REFERENCE_KEY, Results.INFRACTIONS_REFERENCE_ID);
        responseData.put(MessageCommonFields.HAS_INFRACTIONS, Boolean.toString(hasInfraction));
        responseData.put(MessageCommonFields.PURCHASE_ID, purchaseID);

        communicationHandler.sendMessage(MessageType.RESULT, Definitions.PURCHASES_SERVER_NAME, responseData);
        communicationHandler.sendMessage(MessageType.RESULT, Definitions.PRODUCTS_SERVER_NAME, responseData);

        data.put(LocalDefinitions.REPORTED_INFRACTIONS_FIELD, String.valueOf(true));

        return true;
    }
}
