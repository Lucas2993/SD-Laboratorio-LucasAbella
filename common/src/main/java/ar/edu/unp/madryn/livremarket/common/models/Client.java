package ar.edu.unp.madryn.livremarket.common.models;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

public class Client {
    @Getter
    private ObjectId _id;
    @Getter
    private String id;
    @Getter
    @Setter
    private String name;

    public Client() {
        this._id = new ObjectId();
        this.id = this._id.toString();
    }
}
