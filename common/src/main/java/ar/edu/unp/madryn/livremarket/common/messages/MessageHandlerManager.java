package ar.edu.unp.madryn.livremarket.common.messages;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandlerManager {

    private Map<MessageType, MessageHandler> handlers;

    private static MessageHandlerManager instance;

    public static MessageHandlerManager getInstance() {
        if (instance == null) {
            instance = new MessageHandlerManager();
        }
        return instance;
    }

    private MessageHandlerManager() {
        this.handlers = new ConcurrentHashMap<>();
    }

    // Handlers

    public boolean registerHandler(MessageHandler handler, MessageType... types) {
        for(MessageType type : types) {
            if (this.handlers.containsKey(type)) {
                continue;
            }

            this.handlers.put(type, handler);
        }

        return true;
    }

    public MessageHandler getHandlerForType(MessageType type) {
        return this.handlers.get(type);
    }
}
