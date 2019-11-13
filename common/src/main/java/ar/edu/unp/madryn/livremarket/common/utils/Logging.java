package ar.edu.unp.madryn.livremarket.common.utils;

import org.apache.commons.lang3.ArrayUtils;

public class Logging {
    private static final String DEFAULT_SEPARATOR = " ";

    public static void info(String... messages){
        if(ArrayUtils.isEmpty(messages)){
            return;
        }

        String message = String.join(DEFAULT_SEPARATOR, messages);

        String workerID = getWorkerID();

        System.out.println(workerID + DEFAULT_SEPARATOR + message);
    }

    public static void error(String... messages){
        if(ArrayUtils.isEmpty(messages)){
            return;
        }

        String message = String.join(DEFAULT_SEPARATOR, messages);

        String workerID = getWorkerID();

        System.err.println(workerID + DEFAULT_SEPARATOR + message);
    }

    private static String getWorkerID(){
        return "[Worker: " + Thread.currentThread().getId() + "]";
    }
}
