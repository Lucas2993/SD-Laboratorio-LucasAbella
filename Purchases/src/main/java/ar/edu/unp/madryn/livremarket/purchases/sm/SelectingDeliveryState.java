package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class SelectingDeliveryState extends State {

    public SelectingDeliveryState() {
        super("selecting_delivery");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Determinar metodo de envio mediante los datos de simulacion.
         */
        return true;
    }
}
