package ar.edu.unp.madryn.livremarket.products.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class WaitingBookedShipmentState extends State {

    public WaitingBookedShipmentState() {
        super("waiting_booked_shipment");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        System.out.println("Esperando confirmacion de envio agendado!");

        return true;
    }
}
