package ar.edu.unp.madryn.livremarket.common.messages;

import java.util.Map;

public interface MessageHandler {
    void processRequest(Map<String, String> data);
    void processInformation(Map<String, String> data);
    void processControl(Map<String, String> data);
    void processMonitor(Map<String, String> data);
}
