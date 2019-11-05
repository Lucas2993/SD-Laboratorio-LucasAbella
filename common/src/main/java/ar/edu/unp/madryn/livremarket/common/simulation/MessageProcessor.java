package ar.edu.unp.madryn.livremarket.common.simulation;

import java.util.Map;

public interface MessageProcessor {
    PendingOperation processMessage(Map<String,String> messageData);
}
