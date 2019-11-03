package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class RequestingShippingScheduleState extends State {

    public RequestingShippingScheduleState() {
        super("requesting_shipping_schedule");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        return true;
    }
}
