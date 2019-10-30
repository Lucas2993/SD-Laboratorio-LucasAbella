package ar.edu.unp.madryn.livremarket.products.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.products.utils.LocalDefinitions;

import java.util.Map;

public class SendingProductState extends State {

    public SendingProductState() {
        super("sending_product");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        System.out.println("Enviando producto!");

        data.put(LocalDefinitions.PRODUCT_SENT_FIELD, String.valueOf(true));

        System.out.println("Producto enviado!");

        return true;
    }
}
