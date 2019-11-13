package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;

import java.util.Map;

public class PurchaseConfirmedState extends State {

    public PurchaseConfirmedState() {
        super("purchase_confirmed");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        No se hace nada.
         */

        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        Logging.info("Compra confirmada! (ID = " + purchaseID + ")");

        return true;
    }
}
