package ar.edu.unp.madryn.livremarket.common.threads;

import ar.edu.unp.madryn.livremarket.common.clocks.VectorClockController;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandlerManager;
import com.google.gson.Gson;
import lombok.Setter;

public abstract class MessageWorker extends Thread {
    static final String ROUTING_KEY_SEPARATOR_REGEX = "\\.";
    static final String CLOCK_FIELD = "_clock_";

    String consumerTag;

    Gson gson;

    @Setter
    static MessageHandlerManager messageHandlerManager;
    @Setter
    static VectorClockController vectorClockController;

    public MessageWorker(String consumerTag) {
        this.consumerTag = consumerTag;
        this.gson = new Gson();
    }
}
