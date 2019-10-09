package ar.edu.unp.madryn.livremarket.common.messages.types;

import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public abstract class Information implements MessageHandler {

    @Override
    public void handle(Map<String, String> data) {
        String id = data.get(Definitions.INFORMATION_REFERENCE_KEY);
        if (StringUtils.isEmpty(id)) {
            return;
        }
        data.remove(Definitions.INFORMATION_REFERENCE_KEY);

        this.process(id, data);
    }

    public abstract void process(String id, Map<String, String> data);
}
