package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;

import java.util.Map;

public class ReportingRejectedPaymentState extends State {

    public ReportingRejectedPaymentState() {
        super("reporting_rejected_payment");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {

        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        Logging.info("Compra finalizada por pago rechazado! (ID = " + purchaseID + ")");
        return true;
    }
}
