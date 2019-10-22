package ar.edu.unp.madryn.livremarket.common.db;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import org.apache.commons.lang3.math.NumberUtils;

public class DataProviderFactory {
    private static final String CONNECTION_IP = "db_ip";
    private static final String CONNECTION_PORT = "db_port";
    private static final int DEFAULT_PORT = 27017;

    private static DataProviderFactory instance;

    private DataProviderFactory(){

    }

    public static DataProviderFactory getInstance() {
        if(instance == null){
            instance = new DataProviderFactory();
        }

        return instance;
    }

    public DataProvider getProviderInstance(ConfigurationSection connectionConfiguration, String databaseName){
        if(connectionConfiguration == null) {
            return null;
        }

        String ip = connectionConfiguration.getValue(CONNECTION_IP);
        String port = connectionConfiguration.getValue(CONNECTION_PORT);

        return new MongoProvider(ip, NumberUtils.toInt(port, DEFAULT_PORT), databaseName);
    }
}
