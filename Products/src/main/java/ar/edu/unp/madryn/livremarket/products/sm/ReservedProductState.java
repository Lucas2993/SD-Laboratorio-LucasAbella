package ar.edu.unp.madryn.livremarket.products.sm;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.models.Product;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.data.ProductManager;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import ar.edu.unp.madryn.livremarket.products.utils.LocalDefinitions;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class ReservedProductState extends State {
    @Setter
    ProductManager productManager;

    public ReservedProductState() {
        super("reserved_product");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Buscar producto.
        Descontar Stock.
        Guardar datos en la base de datos.
         */

        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);
        String productID = data.get(MessageCommonFields.PRODUCT_ID);
        int amount = MapUtils.getIntValue(data, MessageCommonFields.PRODUCT_AMOUNT, 1);

        if (StringUtils.isEmpty(productID)) {
            Logging.error("Error: No existe un ID de producto a reservar!");
            return false;
        }

        Logging.info("Reservando producto! (ID = " + purchaseID + ")");

        Product product = productManager.findProductByID(productID);

        if(product == null){
            Logging.error("Error: El producto a reservar con ID " + productID + " no se encuentra registrado!");
            return false;
        }

        // Descontar Stock.
        if(!product.subtractStock(amount)){
            Logging.error("Error: El producto a reservar con ID " + productID + " no tiene suficiente stock!");
            return false;
        }

        this.productManager.updateProduct(product);

        Logging.info("Producto reservado correctamente! (ID = " + purchaseID + ")");

        data.put(LocalDefinitions.RESERVED_PRODUCT_FIELD, String.valueOf(true));

        return true;
    }
}
