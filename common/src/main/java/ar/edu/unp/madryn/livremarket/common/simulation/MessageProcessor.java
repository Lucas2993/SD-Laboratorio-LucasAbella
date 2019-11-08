package ar.edu.unp.madryn.livremarket.common.simulation;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public abstract class MessageProcessor {

    public PendingOperation processMessage(Map<String,String> messageData){
        String type = messageData.get(MessageCommonFields.MESSAGE_TYPE_ID);
        if(StringUtils.isEmpty(type)){
            return null;
        }

        MessageType messageType = MessageType.fromTopic(type);
        if(messageType == null){
            return null;
        }

        messageData.remove(MessageCommonFields.MESSAGE_TYPE_ID);

        switch (messageType){
            case GENERAL:
                String operation = messageData.get(Definitions.REQUEST_OPERATION_KEY);
                if (StringUtils.isEmpty(operation)) {
                    return null;
                }
                messageData.remove(Definitions.REQUEST_OPERATION_KEY);

                return this.processGeneralMessage(operation, messageData);
            case RESULT:
                String id = messageData.get(Definitions.INFORMATION_REFERENCE_KEY);
                if (StringUtils.isEmpty(id)) {
                    return null;
                }
                messageData.remove(Definitions.INFORMATION_REFERENCE_KEY);

                return this.processResultMessage(id, messageData);
            default:
                return null;
        }
    }

    public abstract PendingOperation processGeneralMessage(String operation, Map<String, String> data);
    public abstract PendingOperation processResultMessage(String id, Map<String, String> data);
}
