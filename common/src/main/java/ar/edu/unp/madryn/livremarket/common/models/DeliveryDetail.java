package ar.edu.unp.madryn.livremarket.common.models;

import lombok.Getter;
import lombok.Setter;

public class DeliveryDetail {
    @Getter
    @Setter
    private DeliveryMethod deliveryMethod;
    @Getter
    @Setter
    private double cost;

    public DeliveryDetail() {
    }
}
