package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.data.PurchaseManager;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.models.*;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

public class SelectingPaymentState extends State {
    private static final double DEFAULT_CASH_PROBABILITY = 0.5;

    @Setter
    private PurchaseManager purchaseManager;
    @Setter
    private ConfigurationSection simulationConfiguration;

    public SelectingPaymentState() {
        super("selecting_payment");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Seleccionar metodo de pago segun los parametros de simulacion.
         */

        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        Logging.info("Seleccionando metodo de pago! (ID = " + purchaseID + ")");

        Purchase purchase = this.purchaseManager.findPurchaseByID(purchaseID);

        if(purchase == null){
            Logging.error("Error: La compra no pudo ser recuperada de la base de datos!");
            return false;
        }

        String probabilityString = simulationConfiguration.getValue("cash_probability");
        double cashPaymentProbability = NumberUtils.toDouble(probabilityString, DEFAULT_CASH_PROBABILITY);

        PaymentDetail paymentDetail = new PaymentDetail();

        PaymentMethod paymentMethod = (Math.random() < cashPaymentProbability) ? PaymentMethod.CASH : PaymentMethod.CREDIT_CARD;

        paymentDetail.setPaymentMethod(paymentMethod);

        purchase.setPaymentDetail(paymentDetail);

        if(!this.purchaseManager.updatePurchase(purchase)){
            Logging.error("Error: La compra no pudo ser actualizada en la base de datos!");
            return false;
        }

        data.put(MessageCommonFields.PAYMENT_METHOD, paymentMethod.toString());

        Logging.info("Metodo de pago seleccionado correctamente, es '" + paymentMethod + "' (ID = " + purchaseID + ")");

        return true;
    }
}
