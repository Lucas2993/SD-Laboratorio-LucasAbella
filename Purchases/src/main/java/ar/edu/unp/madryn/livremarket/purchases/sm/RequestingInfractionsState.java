package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class RequestingInfractionsState extends State {

    public RequestingInfractionsState() {
        super("requesting_infractions");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Solicitar informacion sobre infracciones.
         */
        return true;
    }
}
