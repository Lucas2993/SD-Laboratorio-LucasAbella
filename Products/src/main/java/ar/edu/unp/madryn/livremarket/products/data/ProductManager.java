package ar.edu.unp.madryn.livremarket.products.data;

import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.models.Product;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

public class ProductManager {
    @Setter
    private DataProvider dataProvider;
    @Getter
    private Collection<Product> products;

    private static ProductManager instance;

    private ProductManager() {
        this.products = new ArrayList<>();
    }

    public static ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }

        return instance;
    }

    public void load() {
        this.products = this.dataProvider.getCollection(Definitions.PRODUCTS_COLLECTION_NAME, Product.class);
    }

    public Product findProductByID(String id) {
        return this.products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean updateProduct(Product product) {
        return this.dataProvider.updateElement(product.getId(), product, Definitions.PRODUCTS_COLLECTION_NAME);
    }
}