package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class PurchaseCompletedState extends State {

    public PurchaseCompletedState() {
        super("purchase_completed");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return null;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        return null;
    }
}
