package ar.edu.unp.madryn.livremarket.deliveries.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class DeliveryDetail {
    @Getter
    @Setter
    private String purchaseID;
    @Getter
    @Setter
    private Date date;

    public DeliveryDetail() {
    }
}
