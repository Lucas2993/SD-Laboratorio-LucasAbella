package ar.edu.unp.madryn.livremarket.common.models;

import lombok.Getter;
import lombok.Setter;

public class Product {
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
    }
}
