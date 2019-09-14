package ar.edu.unp.madryn.livremarket.common.control;

public interface Controllable {
    boolean reset();
    boolean step();
    boolean saveState();
}
