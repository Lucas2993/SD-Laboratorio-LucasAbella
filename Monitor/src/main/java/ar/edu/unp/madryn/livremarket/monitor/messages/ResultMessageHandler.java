package ar.edu.unp.madryn.livremarket.monitor.messages;

import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.Map;

public class ResultMessageHandler implements MessageHandler {

    public ResultMessageHandler() {
    }

    @Override
    public void handle(Map<String, String> data) {
        String serverID = data.get(MessageCommonFields.SOURCE_SERVER);
        String resultMessage = data.get(MessageCommonFields.RESULT_MESSAGE);
        if(StringUtils.isEmpty(serverID) || StringUtils.isEmpty(resultMessage)){
            return;
        }

        Logging.info("Se informo la operacion realizada por el servidor", serverID, "con el resultado", resultMessage);

        JOptionPane.showMessageDialog(null, resultMessage + " (Servidor = " + serverID + ")");
    }
}
