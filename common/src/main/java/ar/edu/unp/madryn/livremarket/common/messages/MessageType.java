package ar.edu.unp.madryn.livremarket.common.messages;

public enum MessageType {
    REQUEST("request"),
    INFORMATION("information"),
    CONTROL("control"),
    MONITOR("monitor");

    private String topic;

    MessageType(String topic) {
        this.topic = topic;
    }

    public String topic() {
        return topic;
    }

    public static MessageType fromTopic(String topic){
        MessageType result = null;
        for(MessageType messageType : values()){
            if(topic.equals(messageType.topic)){
                result = messageType;
                break;
            }
        }
        return result;
    }
}
