package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class PurchaseCompletedState extends State {

    public PurchaseCompletedState() {
        super("purchase_completed");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        System.out.println("Compra finalizada exitosamente! (ID = " + purchaseID + ")");

        return true;
    }
}
