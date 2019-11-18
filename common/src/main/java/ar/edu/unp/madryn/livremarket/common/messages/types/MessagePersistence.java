package ar.edu.unp.madryn.livremarket.common.messages.types;

import ar.edu.unp.madryn.livremarket.common.consistency.SnapshotController;
import ar.edu.unp.madryn.livremarket.common.db.DataProvider;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import ar.edu.unp.madryn.livremarket.common.simulation.SimulationController;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class MessagePersistence implements MessageHandler {
    @Setter
    private DataProvider dataProvider;
    @Setter
    private SimulationController simulationController;
    @Setter
    private SnapshotController snapshotController;

    @Override
    public void handle(Map<String, String> data) {
        String serverID = data.get(MessageCommonFields.SOURCE_SERVER);
        if(!StringUtils.isEmpty(serverID) && this.snapshotController.isTakingSnapshot()) {
            this.snapshotController.handleMessage(serverID, data);
        }

        this.dataProvider.insertElement(data, Definitions.PENDING_MESSAGES_COLLECTION_NAME);

        this.simulationController.execute();
    }
}
