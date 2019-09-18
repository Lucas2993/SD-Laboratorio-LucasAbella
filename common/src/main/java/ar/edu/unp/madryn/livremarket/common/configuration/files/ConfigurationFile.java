package ar.edu.unp.madryn.livremarket.common.configuration.files;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationFile extends ConfigurationSection {
    private static final String FILE_EXTENSION = "properties";

    private String filePath;
    private ClassLoader classLoader;

    public ConfigurationFile(String name, String... folders) {
        super(name);
        this.filePath = "";
        if(folders != null && !ArrayUtils.isEmpty(folders)){
            this.filePath += String.join(File.separator, folders);
            this.filePath += File.separator;
        }
        this.filePath += this.name + "." + FILE_EXTENSION;
        this.classLoader = this.getClass().getClassLoader();
    }

    @Override
    public boolean load() {
        File file = new File(this.filePath);
        if (!FileGeneralUtils.fileExist(file)) {
            InputStream resourceFileContent = this.classLoader.getResourceAsStream(this.filePath);
            if (resourceFileContent == null) {
                return false;
            }

            try {
                FileUtils.copyInputStreamToFile(resourceFileContent, file);
            } catch (Exception e) {
                return false;
            }
        }

        Properties propertiesFileContent = new Properties();

        try {
            InputStream input = new FileInputStream(file);

            // load a properties file
            propertiesFileContent.load(input);

            propertiesFileContent.forEach((key, value) -> this.data.put((String) key, (String) value));
        } catch (IOException ex) {
            return false;
        }

        return true;
    }
}
