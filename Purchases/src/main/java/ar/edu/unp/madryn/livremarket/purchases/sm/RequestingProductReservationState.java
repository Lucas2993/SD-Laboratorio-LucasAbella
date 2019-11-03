package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class RequestingProductReservationState extends State {

    public RequestingProductReservationState() {
        super("requesting_product_reservation");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Enviar solicitud de reservacion de producto.
         */
        return true;
    }
}
