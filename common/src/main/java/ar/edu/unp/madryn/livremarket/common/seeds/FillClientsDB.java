package ar.edu.unp.madryn.livremarket.common.seeds;

import ar.edu.unp.madryn.livremarket.common.data.ClientManager;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.db.MongoConnection;
import ar.edu.unp.madryn.livremarket.common.db.MongoProvider;
import ar.edu.unp.madryn.livremarket.common.models.Client;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;

public class FillClientsDB {
    public static void main(String [] args){

        /*

         */

        Client client = new Client();
        client.setName("Stacy B. Edwards");

        Client client1 = new Client();
        client1.setName("Cassidy C. Espinoza");

        Client client2 = new Client();
        client2.setName("Sebastian I. Hutchinson");

        Client client3 = new Client();
        client3.setName("Cody G. Galloway");

        Client client4 = new Client();
        client4.setName("Daquan T. Finley");

        Client client5 = new Client();
        client5.setName("Lael C. Walls");

        Client client6 = new Client();
        client6.setName("Cameran G. Stein");

        Client client7 = new Client();
        client7.setName("TaShya V. Parks");

        Client client8 = new Client();
        client8.setName("Lev I. Nielsen");

        Client client9 = new Client();
        client9.setName("Angela R. Massey");


        MongoConnection mongoConnection = new MongoConnection("localhost", 27017);

        DataProvider dataProvider = new MongoProvider(mongoConnection, Definitions.COMMON_DATABASE_NAME);

        if(!dataProvider.connect()){
            Logging.error("No se pudo establecer conexion con el servidor de base de datos!");
            return;
        }

        ClientManager clientManager = ClientManager.getInstance();

        clientManager.setDataProvider(dataProvider);

        clientManager.storeClient(client);
        clientManager.storeClient(client1);
        clientManager.storeClient(client2);
        clientManager.storeClient(client3);
        clientManager.storeClient(client4);
        clientManager.storeClient(client5);
        clientManager.storeClient(client6);
        clientManager.storeClient(client7);
        clientManager.storeClient(client8);
        clientManager.storeClient(client9);

        //clientManager.load();

        Logging.info("Fin");
    }
}
