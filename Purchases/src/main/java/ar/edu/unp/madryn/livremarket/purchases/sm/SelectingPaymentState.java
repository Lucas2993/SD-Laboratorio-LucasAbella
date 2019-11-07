package ar.edu.unp.madryn.livremarket.purchases.sm;

import ar.edu.unp.madryn.livremarket.common.data.PurchaseManager;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.models.*;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import lombok.Setter;

import java.util.Map;

public class SelectingPaymentState extends State {

    @Setter
    private PurchaseManager purchaseManager;

    public SelectingPaymentState() {
        super("selecting_payment");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Seleccionar metodo de pago segun los parametros de simulacion.
         */

        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        System.out.println("Seleccionando metodo de pago! (ID = " + purchaseID + ")");

        Purchase purchase = this.purchaseManager.findProductByID(purchaseID);

        if(purchase == null){
            System.err.println("Error: La compra no pudo ser recuperada de la base de datos!");
            return false;
        }

        double cashPaymentProbability = 0.5;

        PaymentDetail paymentDetail = new PaymentDetail();

        PaymentMethod paymentMethod = (Math.random() < cashPaymentProbability) ? PaymentMethod.CASH : PaymentMethod.CREDIT_CARD;

        paymentDetail.setPaymentMethod(paymentMethod);

        purchase.setPaymentDetail(paymentDetail);

        if(!this.purchaseManager.updateProduct(purchase)){
            System.err.println("Error: La compra no pudo ser actualizada en la base de datos!");
            return false;
        }

        data.put(MessageCommonFields.PAYMENT_METHOD, paymentMethod.toString());

        System.out.println("Metodo de pago seleccionado correctamente, es '" + paymentMethod + "' (ID = " + purchaseID + ")");

        return true;
    }
}
