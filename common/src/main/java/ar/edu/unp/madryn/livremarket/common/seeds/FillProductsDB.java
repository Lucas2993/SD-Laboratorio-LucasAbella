package ar.edu.unp.madryn.livremarket.common.seeds;

import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.db.MongoConnection;
import ar.edu.unp.madryn.livremarket.common.db.MongoProvider;
import ar.edu.unp.madryn.livremarket.common.models.Product;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.common.data.ProductManager;

public class FillProductsDB {
    public static void main(String [] args){
        Product product = new Product();

        product.setName("Monitor");
        product.setDescription("Monitor LG");
        product.setStock(20);

        Product product1 = new Product();

        product1.setName("Libro: Sistemas Operativos Distribuidos");
        product1.setDescription("Tanenbaum");
        product1.setStock(20);

        Product product2 = new Product();

        product2.setName("Notebook");
        product2.setDescription("Notebook marca ASUS");
        product2.setStock(20);

        Product product3 = new Product();

        product3.setName("Reloj");
        product3.setDescription("Reloj pulcera Casio");
        product3.setStock(20);


        Product product4 = new Product();

        product4.setName("Mouse");
        product4.setDescription("Mouse inalambrico Genius");
        product4.setStock(20);


        Product product5 = new Product();

        product5.setName("Televisor");
        product5.setDescription("Televisor de 27 pulgadas marca Sanyo");
        product5.setStock(20);


        Product product6 = new Product();

        product6.setName("Termo");
        product6.setDescription("Termo de 1.5L marca Stanley");
        product6.setStock(20);


        Product product7 = new Product();

        product7.setName("Impresora");
        product7.setDescription("Impresora multifuncion marca Samsung");
        product7.setStock(20);

        MongoConnection mongoConnection = new MongoConnection("localhost", 27017);

        DataProvider dataProvider = new MongoProvider(mongoConnection, Definitions.COMMON_DATABASE_NAME);

        if(!dataProvider.connect()){
            System.err.println("No se pudo establecer conexion con el servidor de base de datos!");
            return;
        }

        ProductManager productManager = ProductManager.getInstance();

        productManager.setDataProvider(dataProvider);

//        productManager.storeProduct(product);
//        productManager.storeProduct(product1);
//        productManager.storeProduct(product2);
//        productManager.storeProduct(product3);
//        productManager.storeProduct(product4);
//        productManager.storeProduct(product5);
//        productManager.storeProduct(product6);
//        productManager.storeProduct(product7);

        productManager.load();

        for(Product product8 : productManager.getProducts()){
            System.out.println("Dato 1: " + product8.getId());
            System.out.println("Dato 2: " + product8.getName());
            System.out.println("Dato 3: " + product8.getDescription());
            System.out.println("Dato 4: " + product8.getStock());
        }

        System.out.println("Fin");
    }
}
