package ar.edu.unp.madryn.livremarket.products.sm;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.models.Product;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.data.ProductManager;
import ar.edu.unp.madryn.livremarket.products.utils.LocalDefinitions;
import lombok.Setter;
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
        String productID = data.get(MessageCommonFields.PRODUCT_ID);

        if (StringUtils.isEmpty(productID)) {
            // TODO Dar mensaje de error. Tambien ver que se hace con la solicitud y la compra.
            return false;
        }

        Product product = productManager.findProductByID(productID);

        if(product == null){
            // TODO Error de producto no encontrado.
            return false;
        }

        // Descontar Stock.
        product.subtractStock();
        this.productManager.updateProduct(product);
        // TODO Guardar datos de la transaccion en la base de datos.

        data.put(LocalDefinitions.RESERVED_PRODUCT_FIELD, String.valueOf(true));

        return true;
    }
}
