package ar.edu.unp.madryn.livremarket.common.messages;

public interface MessageDelivery {
    void processMessage(String consumerTag, String message);
}
