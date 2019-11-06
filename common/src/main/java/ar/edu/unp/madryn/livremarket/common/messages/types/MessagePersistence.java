package ar.edu.unp.madryn.livremarket.common.messages.types;

import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Setter;

import java.util.Map;

public class MessagePersistence implements MessageHandler {
    @Setter
    private DataProvider dataProvider;

    @Override
    public void handle(Map<String, String> data) {
        this.dataProvider.insertElement(data, Definitions.PENDING_MESSAGES_COLLECTION_NAME);
    }
}
