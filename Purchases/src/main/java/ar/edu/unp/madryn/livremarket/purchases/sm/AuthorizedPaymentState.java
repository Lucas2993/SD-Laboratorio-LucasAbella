package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class AuthorizedPaymentState extends State {

    public AuthorizedPaymentState() {
        super("authorized_payment");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {

        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        System.out.println("El pago fue autorizado! (ID = " + purchaseID + ")");

        return true;
    }
}
