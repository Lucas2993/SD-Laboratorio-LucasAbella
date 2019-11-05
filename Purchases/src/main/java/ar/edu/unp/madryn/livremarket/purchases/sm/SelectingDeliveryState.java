package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.data.PurchaseManager;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.models.DeliveryDetail;
import ar.edu.unp.madryn.livremarket.common.models.DeliveryMethod;
import ar.edu.unp.madryn.livremarket.common.models.Purchase;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

public class SelectingDeliveryState extends State {
    private static final double DEFAULT_MAIL_PROBABILITY = 0.5;

    @Setter
    private PurchaseManager purchaseManager;
    @Setter
    private ConfigurationSection simulationConfiguration;

    public SelectingDeliveryState() {
        super("selecting_delivery");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Determinar metodo de envio mediante los datos de simulacion.
         */

        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        System.out.println("Seleccionando metodo de entrega! (ID = " + purchaseID + ")");

        Purchase purchase = this.purchaseManager.findProductByID(purchaseID);

        if(purchase == null){
            System.err.println("Error: La compra no pudo ser recuperada de la base de datos!");
            return false;
        }

        String probabilityString = simulationConfiguration.getValue("mail_probability");
        double deliveryProbability = NumberUtils.toDouble(probabilityString, DEFAULT_MAIL_PROBABILITY);

        DeliveryDetail deliveryDetail = new DeliveryDetail();

        DeliveryMethod deliveryMethod = (Math.random() < deliveryProbability) ? DeliveryMethod.MAIL : DeliveryMethod.OTHER;

        deliveryDetail.setDeliveryMethod(deliveryMethod);

        purchase.setDeliveryDetail(deliveryDetail);

        if(!this.purchaseManager.updateProduct(purchase)){
            System.err.println("Error: La compra no pudo ser actualizada en la base de datos!");
            return false;
        }

        data.put(MessageCommonFields.NEEDS_SHIPPING, String.valueOf(deliveryMethod.equals(DeliveryMethod.MAIL)));

        System.out.println("Metodo de entrega seleccionado correctamente!");

        return true;
    }
}
