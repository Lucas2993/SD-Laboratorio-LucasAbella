package ar.edu.unp.madryn.livremarket.common.server;

public abstract class Server {
    private String ipAddress;
    private int port;

    public abstract boolean connect();
}
