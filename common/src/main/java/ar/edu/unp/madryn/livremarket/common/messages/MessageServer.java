package ar.edu.unp.madryn.livremarket.common.messages;

public interface MessageServer {
    boolean connect();
    boolean disconnect();

    boolean registerProcessor(String bindingKey, MessageDelivery messageProcessor);
    boolean sendMessage(String routingKey, String message);
}
