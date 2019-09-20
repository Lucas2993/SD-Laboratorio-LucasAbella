package ar.edu.unp.madryn.livremarket.common.configuration;

import java.util.HashMap;
import java.util.Map;

public abstract class ConfigurationSection {
    public static final String CONFIGURATION_FOLDER = "configuration";

    protected String name;
    protected Map<String,String> data;

    public ConfigurationSection(String name){
        this.name = name;
        this.data = new HashMap<>();
    }

    public String getValue(String key){
        if(this.data == null || this.data.isEmpty()){
            return null;
        }
        return this.data.get(key);
    }

    public abstract boolean load();
}
