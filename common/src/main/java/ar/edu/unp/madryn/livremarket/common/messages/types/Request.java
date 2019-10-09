package ar.edu.unp.madryn.livremarket.common.messages.types;

import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public abstract class Request implements MessageHandler {

    @Override
    public void handle(Map<String, String> data) {
        String operation = data.get(Definitions.REQUEST_OPERATION_KEY);
        if (StringUtils.isEmpty(operation)) {
            return;
        }
        data.remove(Definitions.REQUEST_OPERATION_KEY);

        this.execute(operation, data);
    }

    public abstract void execute(String operation, Map<String, String> data);
}
