package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class SelectingPaymentState extends State {

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
        return true;
    }
}
