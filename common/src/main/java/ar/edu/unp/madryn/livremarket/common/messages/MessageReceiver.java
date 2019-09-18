package ar.edu.unp.madryn.livremarket.common.messages;

public abstract class MessageReceiver extends MessageServer{

    public MessageReceiver(String topic) {
        super(topic);
    }

    public abstract void registerProcessor(MessageDelivery messageProcessor);
}
