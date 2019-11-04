package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class ReportedPaymentAuthorizationState extends State {

    public ReportedPaymentAuthorizationState() {
        super("reported_payment_authorization");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {

        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        System.out.println("Informacion de autorizacion de pago recibida! (ID = " + purchaseID + ")");

        return true;
    }
}
