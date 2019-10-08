package ar.edu.unp.madryn.livremarket.common.models;

import lombok.Getter;
import lombok.Setter;

public class PaymentDetail {
    @Getter
    @Setter
    private PaymentMethod paymentMethod;
    @Getter
    @Setter
    private Boolean authorized;

    public PaymentDetail() {
    }
}
