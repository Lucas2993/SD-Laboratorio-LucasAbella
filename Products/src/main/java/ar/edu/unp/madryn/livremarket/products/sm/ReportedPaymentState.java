package ar.edu.unp.madryn.livremarket.products.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class ReportedPaymentState extends State {

    public ReportedPaymentState() {
        super("reported_payment");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        System.out.println("Informacion de pago obtenida!");
        return true;
    }
}
