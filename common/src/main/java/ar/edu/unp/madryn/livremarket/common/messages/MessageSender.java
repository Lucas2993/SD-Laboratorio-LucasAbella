package ar.edu.unp.madryn.livremarket.common.messages;

public abstract class MessageSender extends MessageServer {

    public MessageSender(String topic) {
        super(topic);
    }

    public abstract boolean sendMessage(String message);
}
