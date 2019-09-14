package ar.edu.unp.madryn.livremarket.common.monitor;

import java.util.Collection;

public interface Monitorable {
    ServerState getCurrentState();

    Collection<ServerState> getSavedStates();
}
