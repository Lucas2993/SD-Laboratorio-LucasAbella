package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class ReportingInfractionsState extends State {

    public ReportingInfractionsState() {
        super("reporting_infractions");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {

        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        System.out.println("Compra finalizada con infracciones! (ID = " + purchaseID + ")");

        return true;
    }
}
