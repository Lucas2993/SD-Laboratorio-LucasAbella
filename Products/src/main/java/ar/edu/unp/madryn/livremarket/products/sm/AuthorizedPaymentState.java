package ar.edu.unp.madryn.livremarket.products.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;

import java.util.Map;

public class AuthorizedPaymentState extends State {

    public AuthorizedPaymentState() {
        super("authorized_payment");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        Logging.info("La compra tiene el pago autorizado!");

        return true;
    }
}
