package ar.edu.unp.madryn.livremarket.common.sm;

import lombok.Getter;

import java.util.Objects;

/**
 * @author iMinecrafting
 * Representa un estado utilizable dentro una maquina de estados.
 */
public abstract class State {
    @Getter
    private String identifier;

    public State(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Permite resetear el estado interno del estado.
     * @return Verdadero si el reseteo pudo realizarse correctamente.
     */
    public abstract Boolean reset();

    /**
     * Ejecuta un proceso correspondiente al ingreso al estado.
     * @return Verdadero si el proceso se ejecuto sin ningun inconveniente.
     */
    public abstract Boolean process();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        State state = (State) o;
        return Objects.equals(getIdentifier(), state.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier());
    }
}
