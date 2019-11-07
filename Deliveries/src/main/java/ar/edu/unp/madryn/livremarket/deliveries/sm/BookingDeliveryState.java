package ar.edu.unp.madryn.livremarket.deliveries.sm;

import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.deliveries.models.DeliveryDetail;
import ar.edu.unp.madryn.livremarket.deliveries.utils.LocalDefinitions;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookingDeliveryState extends State {

    @Setter
    private DataProvider dataProvider;

    public BookingDeliveryState() {
        super("booking_delivery");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        System.out.println("Agendando envio! (ID = " + purchaseID + ")");

        Map<String, String> resultData = new HashMap<>();

        DeliveryDetail deliveryDetail = new DeliveryDetail();

        deliveryDetail.setPurchaseID(purchaseID);

        deliveryDetail.setDate(DateUtils.addDays(new Date(), 3));

        this.dataProvider.insertElement(deliveryDetail, Definitions.BOOKED_DELIVERIES_COLLECTION_NAME);

        System.out.println("Envio agendado! (ID =" + purchaseID + ")");

        data.put(LocalDefinitions.BOOKED_DELIVERY_FIELD, String.valueOf(true));

        return true;
    }
}
