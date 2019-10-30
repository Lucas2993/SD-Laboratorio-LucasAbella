package ar.edu.unp.madryn.livremarket.products.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.products.utils.LocalDefinitions;

import java.util.Map;

public class ReleasingProductState extends State {
    public ReleasingProductState() {
        super("releasing_product");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Buscar producto
        Actualizar stock
         */

        System.out.println("Liberando producto!");

        // TODO Implementar liberacion del producto

        data.put(LocalDefinitions.RESERVED_PRODUCT_FIELD, String.valueOf(false));

        return true;
    }
}
