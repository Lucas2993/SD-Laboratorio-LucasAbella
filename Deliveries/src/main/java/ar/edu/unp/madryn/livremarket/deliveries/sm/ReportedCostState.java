package ar.edu.unp.madryn.livremarket.deliveries.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class ReportedCostState extends State {

    public ReportedCostState() {
        super("reported_state");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        System.out.println("Costo de envio informado!");
        return true;
    }
}
