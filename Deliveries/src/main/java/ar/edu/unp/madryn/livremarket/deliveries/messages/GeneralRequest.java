package ar.edu.unp.madryn.livremarket.deliveries.messages;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.messages.Results;
import ar.edu.unp.madryn.livremarket.common.messages.types.Request;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.deliveries.models.DeliveryDetail;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bson.Document;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GeneralRequest extends Request {
    private static final double DEFAULT_MAX_DELIVERY_COST = 100.0;

    @Setter
    private CommunicationHandler communicationHandler;
    @Setter
    private ConfigurationSection simulationConfiguration;

    @Setter
    private DataProvider dataProvider;

    @Override
    public void execute(String operation, Map<String, String> data) {
        System.out.println("Llego la operacion general '" + operation + "' con los datos: " + data);
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);
        switch (operation) {
            case Operations.GET_DELIVERY_COST_OPERATION:
                Map<String, String> responseData = new HashMap<>();

                responseData.put(Definitions.INFORMATION_REFERENCE_KEY, Results.DELIVERY_COST_REFERENCE_ID);
                responseData.put(MessageCommonFields.DELIVERY_COST, String.valueOf(this.calculateCost()));
                if (!StringUtils.isEmpty(purchaseID)) {
                    responseData.put(MessageCommonFields.PURCHASE_ID, purchaseID);
                }

                communicationHandler.sendMessage(MessageType.RESULT, Definitions.PURCHASES_SERVER_NAME, responseData);

                break;
            case Operations.BOOK_SHIPMENT_OPERATION:
                Map<String, String> resultData = new HashMap<>();

                DeliveryDetail deliveryDetail = new DeliveryDetail();

                resultData.put(Definitions.INFORMATION_REFERENCE_KEY, Results.BOOK_SHIPMENT_REFERENCE_ID);
                if (!StringUtils.isEmpty(purchaseID)) {
                    resultData.put(MessageCommonFields.PURCHASE_ID, purchaseID);
                    deliveryDetail.setPurchaseID(purchaseID);
                }

                deliveryDetail.setDate(DateUtils.addDays(new Date(), 3));

                this.dataProvider.insertElement(deliveryDetail, Definitions.BOOKED_DELIVERIES_COLLECTION_NAME);

                communicationHandler.sendMessage(MessageType.RESULT, Definitions.PRODUCTS_SERVER_NAME, resultData);
                break;
        }
    }

    private double calculateCost() {
        String probabilityString = simulationConfiguration.getValue("max_delivery_cost");
        double probability = NumberUtils.toDouble(probabilityString, DEFAULT_MAX_DELIVERY_COST);

        DecimalFormat df = new DecimalFormat("#.##");

        df.setRoundingMode(RoundingMode.FLOOR);

        return new Double(df.format(Math.random() * probability));
    }
}
