package ar.edu.unp.madryn.livremarket.common.messages;

import lombok.Getter;

public abstract class MessageServer {
    @Getter
    private String topic;

    public MessageServer(String topic) {
        this.topic = topic;
    }

    public abstract boolean connect();
}
