package ar.edu.unp.madryn.livremarket.common.messages;

public enum MessageType {
    STATUS("status"),
    RESULT("result"),
    GENERAL("general"),
    CONTROL("control"),
    MONITOR("monitor"),
    MARK("mark");

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
            if(topic.equalsIgnoreCase(messageType.topic)){
                result = messageType;
                break;
            }
        }
        return result;
    }
}
