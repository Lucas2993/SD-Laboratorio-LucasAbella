package ar.edu.unp.madryn.livremarket.products.messages;

import ar.edu.unp.madryn.livremarket.common.messages.types.Information;

import java.util.Map;

public class ResultInformation extends Information {

    @Override
    public void process(String id, Map<String, String> data) {
        System.out.println("Llego informacion con id '" + id + "' y con los datos: " + data);
    }
}
