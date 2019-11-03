package ar.edu.unp.madryn.livremarket.common.models;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

public class Purchase {
    @Getter
    private ObjectId _id;
    @Getter
    private String id;
    @Getter
    @Setter
    private int units;
    @Getter
    @Setter
    private String clientID;
    @Getter
    @Setter
    private String productID;
    @Getter
    @Setter
    private PaymentDetail paymentDetail;
    @Getter
    @Setter
    private DeliveryDetail deliveryDetail;
    @Getter
    @Setter
    private Boolean infractions;

    public Purchase() {
        this._id = new ObjectId();
        this.id = this._id.toString();
    }
}
