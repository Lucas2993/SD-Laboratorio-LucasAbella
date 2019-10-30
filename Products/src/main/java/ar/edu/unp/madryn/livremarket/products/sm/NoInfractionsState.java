package ar.edu.unp.madryn.livremarket.products.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class NoInfractionsState extends State {

    public NoInfractionsState() {
        super("no_infractions");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        System.out.println("La compra no tiene infracciones!");

        return true;
    }
}
