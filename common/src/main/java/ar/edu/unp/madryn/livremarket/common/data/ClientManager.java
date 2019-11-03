package ar.edu.unp.madryn.livremarket.common.data;

import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.models.Client;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

public class ClientManager {
    @Setter
    private DataProvider dataProvider;
    @Getter
    private Collection<Client> clients;

    private static ClientManager instance;

    private ClientManager() {
        this.clients = new ArrayList<>();
    }

    public static ClientManager getInstance() {
        if (instance == null) {
            instance = new ClientManager();
        }

        return instance;
    }

    public void load() {
        this.clients = this.dataProvider.getCollection(Definitions.CLIENTS_COLLECTION_NAME, Client.class);
    }

    public Client findClientByID(String id) {
        return this.clients.stream()
                .filter(client -> client.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean updateClient(Client client) {
        // TODO No dejar este texto re duro...
        return this.dataProvider.updateElement("id", client.getId(), client, Definitions.CLIENTS_COLLECTION_NAME);
    }

    public boolean storeClient(Client client) {
        return this.dataProvider.insertElement(client, Definitions.CLIENTS_COLLECTION_NAME);
    }
}
