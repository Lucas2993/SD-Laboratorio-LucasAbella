package ar.edu.unp.madryn.livremarket.common.messages;

import java.util.Map;

public interface MessageHandler {
    void handle(Map<String, String> data);
}
