package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;

import java.util.Map;

public class ReportedInfractionsState extends State {

    public ReportedInfractionsState() {
        super("reported_infractions");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Imprimir el resultado de las infracciones.
         */

        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        Logging.info("Infracciones informadas! (ID = " + purchaseID + ")");

        return true;
    }
}
