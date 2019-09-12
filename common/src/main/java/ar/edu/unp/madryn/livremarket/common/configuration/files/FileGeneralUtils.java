package ar.edu.unp.madryn.livremarket.common.configuration.files;

import java.io.File;

public class FileGeneralUtils {
    public static boolean fileExist(String path) {
        File file = new File(path);

        return fileExist(file);
    }

    public static boolean fileExist(File file) {
        return file.exists();
    }
}
