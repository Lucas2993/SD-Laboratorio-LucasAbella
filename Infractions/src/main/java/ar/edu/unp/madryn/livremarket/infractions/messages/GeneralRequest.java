package ar.edu.unp.madryn.livremarket.infractions.messages;

import ar.edu.unp.madryn.livremarket.common.messages.types.Request;

import java.util.Map;

public class GeneralRequest extends Request {

    @Override
    public void execute(String operation, Map<String, String> data) {
        System.out.println("Me llego la operacion '" + operation + "' con los datos: " + data);
    }
}
