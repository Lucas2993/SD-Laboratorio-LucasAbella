package ar.edu.unp.madryn.livremarket.common.messages.types;

import ar.edu.unp.madryn.livremarket.common.consistency.SnapshotController;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class MarkMessage implements MessageHandler {
    @Setter
    private SnapshotController snapshotController;

    @Override
    public void handle(Map<String, String> data) {
        String serverID = data.get(MessageCommonFields.SOURCE_SERVER);
        if(!StringUtils.isEmpty(serverID)) {
            this.snapshotController.handleMark(serverID);
        }
    }
}
