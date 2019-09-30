package ar.edu.unp.madryn.livremarket.common.messages.types;

import ar.edu.unp.madryn.livremarket.common.messages.MessageHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public abstract class Information implements MessageHandler {
    private static final String ID_KEY = "id";

    @Override
    public void handle(Map<String, String> data) {
        String id = data.get(ID_KEY);
        if(StringUtils.isEmpty(id)){
            return;
        }
        data.remove(ID_KEY);

        this.process(id, data);
    }

    public abstract void process(String id, Map<String,String> data);
}
