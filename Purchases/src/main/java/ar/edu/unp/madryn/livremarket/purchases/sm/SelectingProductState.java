package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;

import java.util.Map;

public class SelectingProductState extends State {

    public SelectingProductState() {
        super("selecting_product");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Obtener un producto aleatorio
         */

        Logging.info("Seleccionando producto!");

        return true;
    }
}
