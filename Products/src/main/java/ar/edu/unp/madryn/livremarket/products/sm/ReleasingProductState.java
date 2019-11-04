package ar.edu.unp.madryn.livremarket.products.sm;

import ar.edu.unp.madryn.livremarket.common.data.ProductManager;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.models.Product;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.products.utils.LocalDefinitions;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class ReleasingProductState extends State {

    @Setter
    private ProductManager productManager;

    public ReleasingProductState() {
        super("releasing_product");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        /*
        Buscar producto
        Actualizar stock
         */

        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);
        String productID = data.get(MessageCommonFields.PRODUCT_ID);
        int amount = MapUtils.getIntValue(data, MessageCommonFields.PRODUCT_AMOUNT, 1);

        if (StringUtils.isEmpty(productID)) {
            System.err.println("Error: No existe un ID de producto a liberar!");
            return false;
        }

        System.out.println("Liberando producto! (ID = " + purchaseID + ")");

        Product product = productManager.findProductByID(productID);

        if(product == null){
            System.err.println("Error: El producto a liberar con ID " + productID + " no se encuentra registrado!");
            return false;
        }

        // Descontar Stock.
        product.addStock(amount);

        this.productManager.updateProduct(product);

        System.out.println("Producto liberado correctamente! (ID = " + purchaseID + ")");

        data.put(LocalDefinitions.RESERVED_PRODUCT_FIELD, String.valueOf(false));

        return true;
    }
}
