package ar.edu.unp.madryn.livremarket.common.configuration;

import ar.edu.unp.madryn.livremarket.common.configuration.files.ConfigurationFile;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationManager {
    private Map<String, ConfigurationSection> configurations;

    private static ConfigurationManager instance;

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    private ConfigurationManager() {
        this.configurations = new HashMap<>();
    }

    public ConfigurationSection loadConfiguration(String name, String... folders){
        if(this.configurations.containsKey(name)) {
            return this.configurations.get(name);
        }
        ConfigurationSection configurationSection = new ConfigurationFile(name, folders);

        if(!configurationSection.load()){
            return null;
        }

        this.configurations.put(name, configurationSection);

        return configurationSection;
    }
}
