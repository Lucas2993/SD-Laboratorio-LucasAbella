package ar.edu.unp.madryn.livremarket.common.server;

import ar.edu.unp.madryn.livremarket.common.control.Controllable;
import ar.edu.unp.madryn.livremarket.common.monitor.Monitorable;

public abstract class Server implements Controllable, Monitorable {
    private String ipAddress;
    private int port;

    public abstract boolean connect();
}
