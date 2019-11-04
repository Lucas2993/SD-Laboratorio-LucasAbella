package ar.edu.unp.madryn.livremarket.common.models;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

public class Product {
    @Getter
    @Expose(serialize = false, deserialize = false)
    private ObjectId _id;
    @Getter
    private String id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private int stock;

    public Product() {
        this._id = new ObjectId();
        this.id = this._id.toString();
    }

    public Boolean subtractStock(int amount){
        if(this.stock == 0 || this.stock < amount){
            return false;
        }

        this.stock -= amount;
        return true;
    }

    public Boolean addStock(int amount){
        if(this.stock < 0){
            this.stock = 0;
        }

        this.stock += amount;
        return true;
    }
}
