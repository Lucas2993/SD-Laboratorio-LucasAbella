package ar.edu.unp.madryn.livremarket.common.utils;

import org.apache.commons.lang3.ArrayUtils;

public class Logging {
    private static final String DEFAULT_SEPARATOR = " ";
    private static final int FIRST_INDEX = 0;

    public static void info(String... messages){
        if(ArrayUtils.isEmpty(messages)){
            return;
        }

        String message = String.join(DEFAULT_SEPARATOR, messages);

        System.out.println(message);
    }

    public static void error(String... messages){
        if(ArrayUtils.isEmpty(messages)){
            return;
        }

        String message = String.join(DEFAULT_SEPARATOR, messages);

        System.err.println(message);
    }
}
