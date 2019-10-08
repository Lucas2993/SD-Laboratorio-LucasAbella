package ar.edu.unp.madryn.livremarket.payments.messages;

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
    private static final double DEFAULT_AUTHORIZE_PROBABILITY = 0.5;

    @Setter
    private CommunicationHandler communicationHandler;
    @Setter
    private ConfigurationSection simulationConfiguration;

    @Override
    public void execute(String operation, Map<String, String> data) {
        System.out.println("Llego la operacion general '" + operation + "' con los datos: " + data);
        switch (operation) {
            case Operations.AUTHORIZE_PAYMENT_OPERATION:
                Map<String, String> responseData = new HashMap<>();

                String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);
                responseData.put(Results.PAYMENT_RESULT, Boolean.toString(this.isAuthorized()));
                if (!StringUtils.isEmpty(purchaseID)) {
                    responseData.put(MessageCommonFields.PURCHASE_ID, purchaseID);
                }

                communicationHandler.sendMessage(MessageType.RESULT, Definitions.PURCHASES_SERVER_NAME, responseData);
                communicationHandler.sendMessage(MessageType.RESULT, Definitions.PRODUCTS_SERVER_NAME, responseData);

                break;
        }
    }

    private boolean isAuthorized() {
        String probabilityString = simulationConfiguration.getValue("payment_rejected_probability");
        double probability = NumberUtils.toDouble(probabilityString, DEFAULT_AUTHORIZE_PROBABILITY);

        return (Math.random() < probability);
    }
}
