package ar.edu.unp.madryn.livremarket.common.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public class MongoConnection {
    private String ip;

    private int port;

    @Getter
    private MongoClient client;

    public MongoConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean connect() {
        if (StringUtils.isEmpty(this.ip) || this.port < 0) {
            return false;
        }

        MongoClientOptions.Builder builder = MongoClientOptions.builder().connectTimeout(3000);
        this.client = new MongoClient(new ServerAddress(this.ip, this.port), builder.build());

        return isConnected();
    }

    public boolean isConnected(){
        try{
            this.client.getAddress();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
