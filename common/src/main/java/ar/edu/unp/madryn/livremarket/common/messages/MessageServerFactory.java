package ar.edu.unp.madryn.livremarket.common.messages;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.amqp.AMQPMessageHandler;
import org.apache.commons.lang3.StringUtils;

public class MessageServerFactory {
    private static final String URL_CONFIGURATION_KEY = "amqp_url";
    private static final String EXCHANGE_NAME_CONFIGURATION_KEY = "exchange_name";

    private static MessageServerFactory instance;

    public static MessageServerFactory getInstance() {
        if(instance == null){
            instance = new MessageServerFactory();
        }
        return instance;
    }

    private MessageServerFactory(){

    }

    public MessageServer getNewMessageServer(ConfigurationSection configuration){
        if(configuration == null){
            return null;
        }

        String url = configuration.getValue(URL_CONFIGURATION_KEY);
        String exchangeName = configuration.getValue(EXCHANGE_NAME_CONFIGURATION_KEY);

        if(StringUtils.isEmpty(url) || StringUtils.isEmpty(exchangeName)){
            return null;
        }

        return new AMQPMessageHandler(url, exchangeName);
    }
}
