package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class RequestingDeliveryCostState extends State {

    public RequestingDeliveryCostState() {
        super("requesting_delivery_cost");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Enviar mensaje al servidor de envios
         */
        return true;
    }
}
