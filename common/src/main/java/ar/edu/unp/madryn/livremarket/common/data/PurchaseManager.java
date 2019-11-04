package ar.edu.unp.madryn.livremarket.common.data;

import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.models.Purchase;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

public class PurchaseManager {
    @Setter
    private DataProvider dataProvider;
    @Getter
    private Collection<Purchase> purchases;

    private static PurchaseManager instance;

    private PurchaseManager() {
        this.purchases = new ArrayList<>();
    }

    public static PurchaseManager getInstance() {
        if (instance == null) {
            instance = new PurchaseManager();
        }

        return instance;
    }

    public void load() {
        this.purchases = this.dataProvider.getCollection(Definitions.PURCHASES_COLLECTION_NAME, Purchase.class);
    }

    public Purchase findProductByID(String id) {
        return this.purchases.stream()
                .filter(purchase -> purchase.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean updateProduct(Purchase purchase) {
        // TODO No dejar este texto re duro...
        return this.dataProvider.updateElement("id", purchase.getId(), purchase, Definitions.PURCHASES_COLLECTION_NAME);
    }

    public boolean storeProduct(Purchase purchase) {
        if(!this.purchases.contains(purchase)){
            this.purchases.add(purchase);
        }
        return this.dataProvider.insertElement(purchase, Definitions.PURCHASES_COLLECTION_NAME);
    }
}
