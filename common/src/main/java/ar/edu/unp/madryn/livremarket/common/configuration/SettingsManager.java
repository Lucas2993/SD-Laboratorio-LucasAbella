package ar.edu.unp.madryn.livremarket.common.configuration;

import ar.edu.unp.madryn.livremarket.common.configuration.files.FileGeneralUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SettingsManager {

    private Properties settingsPropertiesContent;
    private ClassLoader classLoader;

    private static SettingsManager instance;

    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    private SettingsManager() {
        this.classLoader = this.getClass().getClassLoader();
    }

    public boolean loadSettings(String fileName) {
        File file = new File(fileName);
        if (!FileGeneralUtils.fileExist(file)) {
            InputStream resourceFileContent = this.classLoader.getResourceAsStream(fileName);
            if (resourceFileContent == null) {
                return false;
            }

            try {
                FileUtils.copyInputStreamToFile(resourceFileContent, file);
            } catch (Exception e) {
                return false;
            }
        }

        this.settingsPropertiesContent = new Properties();

        try {
            InputStream input = new FileInputStream(file);

            // load a properties file
            this.settingsPropertiesContent.load(input);

        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    public String getSetting(String name) {
        if (this.settingsPropertiesContent == null || this.settingsPropertiesContent.isEmpty()) {
            return null;
        }

        return this.settingsPropertiesContent.getProperty(name);
    }
}
