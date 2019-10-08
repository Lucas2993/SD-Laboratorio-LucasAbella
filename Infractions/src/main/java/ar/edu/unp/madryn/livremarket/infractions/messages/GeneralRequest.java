package ar.edu.unp.madryn.livremarket.infractions.messages;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.messages.Results;
import ar.edu.unp.madryn.livremarket.common.messages.types.Request;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;

public class GeneralRequest extends Request {
    private static final double DEFAULT_INFRACTION_PROBABILITY = 0.5;

    @Setter
    private CommunicationHandler communicationHandler;
    @Setter
    private ConfigurationSection simulationConfiguration;

    @Override
    public void execute(String operation, Map<String, String> data) {
        switch (operation) {
            case Operations.GET_INFRACTIONS_OPERATION:
                Map<String, String> responseData = new HashMap<>();

                String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);
                responseData.put(Results.INFRACTIONS_RESULT, Boolean.toString(this.hasInfraction()));
                if (!StringUtils.isEmpty(purchaseID)) {
                    responseData.put(MessageCommonFields.PURCHASE_ID, purchaseID);
                }

                communicationHandler.sendMessage(MessageType.RESULT, Definitions.PURCHASES_SERVER_NAME, responseData);
                communicationHandler.sendMessage(MessageType.RESULT, Definitions.PRODUCTS_SERVER_NAME, responseData);

                break;
        }
    }

    private boolean hasInfraction() {
        String probabilityString = simulationConfiguration.getValue("infraction_probability");
        double probability = NumberUtils.toDouble(probabilityString, DEFAULT_INFRACTION_PROBABILITY);

        return (Math.random() < probability);
    }
}
