package ar.edu.unp.madryn.livremarket.payments.sm;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

public class ResolvingPaymentState extends State {
    private static final double DEFAULT_AUTHORIZE_PROBABILITY = 0.5;

    @Setter
    private ConfigurationSection simulationConfiguration;

    public ResolvingPaymentState() {
        super("resolving_payment");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        Logging.info("Resolviendo pago! (ID = " + purchaseID + ")");

        boolean isAuthorized = this.isAuthorized();

        data.put(MessageCommonFields.AUTHORIZED_PAYMENT, Boolean.toString(isAuthorized));

        Logging.info("Resultado de autorizacion de pago: '" + isAuthorized + "' (ID =" + purchaseID + ")");

        return true;
    }

    private boolean isAuthorized() {
        String probabilityString = simulationConfiguration.getValue("payment_rejected_probability");
        double probability = NumberUtils.toDouble(probabilityString, DEFAULT_AUTHORIZE_PROBABILITY);

        return (Math.random() < probability);
    }
}
