package ar.edu.unp.madryn.livremarket.payments.sm;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.Results;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import ar.edu.unp.madryn.livremarket.payments.utils.LocalDefinitions;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

public class ReportingPaymentState extends State {
    @Setter
    private CommunicationHandler communicationHandler;

    public ReportingPaymentState() {
        super("reporting_payment");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        boolean isAuthorized = MapUtils.getBoolean(data, MessageCommonFields.AUTHORIZED_PAYMENT, false);

        Logging.info("Informando pago (ID =" + purchaseID + ")");

        Map<String, String> responseData = new HashMap<>();

        responseData.put(Definitions.INFORMATION_REFERENCE_KEY, Results.PAYMENT_AUTHORIZATION_REFERENCE_ID);
        responseData.put(MessageCommonFields.AUTHORIZED_PAYMENT, Boolean.toString(isAuthorized));
        responseData.put(MessageCommonFields.PURCHASE_ID, purchaseID);

        communicationHandler.sendMessage(MessageType.RESULT, Definitions.PURCHASES_SERVER_NAME, responseData);
        communicationHandler.sendMessage(MessageType.RESULT, Definitions.PRODUCTS_SERVER_NAME, responseData);

        data.put(LocalDefinitions.REPORTED_PAYMENT_FIELD, String.valueOf(true));

        return true;
    }
}
