package ar.edu.unp.madryn.livremarket.infractions.infractions;

import ar.edu.unp.madryn.livremarket.common.monitor.ServerState;
import ar.edu.unp.madryn.livremarket.common.server.Server;

import java.util.Collection;

public class InfractionsServer extends Server {


    @Override
    public boolean connect() {
        return false;
    }

    @Override
    public boolean reset() {
        return false;
    }

    @Override
    public boolean step() {
        return false;
    }

    @Override
    public boolean saveState() {
        return false;
    }

    @Override
    public ServerState getCurrentState() {
        return null;
    }

    @Override
    public Collection<ServerState> getSavedStates() {
        return null;
    }
}
