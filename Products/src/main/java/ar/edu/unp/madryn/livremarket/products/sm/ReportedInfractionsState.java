package ar.edu.unp.madryn.livremarket.products.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;

import java.util.Map;

public class ReportedInfractionsState extends State {

    public ReportedInfractionsState() {
        super("reported_infractions");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        Logging.info("Informacion de infraciones obtenida!");

        return true;
    }
}
