package ar.edu.unp.madryn.livremarket.common.sm;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author iMinecrafting
 * Representa la organizacion (estados y transiciones) de una maquina de estados. Solo efectua la definicion de la misma.
 */
public class Template {
    private List<State> states;
    private List<Transition> transitions;
    @Getter
    private State initialState;

    public Template() {
        this.states = new ArrayList<>();
        this.transitions = new ArrayList<>();
    }

    /**
     * Agrega un estado a la maquina. Si la maquina aun no tiene estado actual, se setea.
     *
     * @param state Estado a agregar a la maquina.
     * @return Verdadero si el estado pudo ser agregado correctamente.
     */
    public Boolean addState(State state) {
        // Si el estado ya se encontraba registrado.
        if (this.states.contains(state)) {
            return false;
        }

        // Se agrega el estado.
        this.states.add(state);
        // Si no hay un estado inicial aun.
        if (this.initialState == null) {
            // Se setea el estado como el inicial.
            this.initialState = state;
        }

        return true;
    }

    /**
     * Agrega una transicion a la maquina de estados.
     *
     * @param transition Transicion a agregar.
     * @return Verdadero si la transicion pudo ser agregada correctamente.
     */
    private Boolean addTransition(Transition transition) {
        // Si la maquina ya contenia la transicion.
        if (this.transitions.contains(transition)) {
            return false;
        }

        // Se agrega la transicion.
        this.transitions.add(transition);

        return true;
    }

    /**
     * Agrega una transicion a la maquina de estados.
     *
     * @param from      Estado de origen.
     * @param to        Estado de destino.
     * @param condition Condicion a evaluar para realizar la transicion.
     * @return Verdadero si la transicion pudo ser agregada correctamente.
     */
    public Boolean addTransition(State from, State to, Transition.Evaluable condition) {
        if (from == null || to == null || condition == null) {
            return false;
        }

        if (!this.containsState(from) || !this.containsState(to)) {
            return false;
        }

        return this.addTransition(new Transition(from, to, condition));
    }

    /**
     * Realiza la busqueda de una transicion mediante el estado de origen. En el proceso se evalua si la condicion se cumple.
     *
     * @param from Estado del cual se desea transicionar.
     * @return La transicion encontrada. Si no se encontro una transicion valida, se devuelve null.
     */
    Transition searchTransition(State from, Map<String, String> data) {
        Optional<Transition> result = this.transitions.stream()
                .filter(transition -> transition.getFrom().equals(from) && transition.condition(data))
                .findFirst();

        return result.orElse(null);
    }

    /**
     * Determina si hay una transicion que pueda realizarse desde el estado actual. En el proceso se evalua la condicion.
     *
     * @param from Estado del cual se desea transicionar.
     * @return Verdadero si es posible transicionar.
     */
    Boolean hasTransition(State from, Map<String, String> data) {
        return this.transitions.stream()
                .anyMatch(transition -> transition.getFrom().equals(from) && transition.condition(data));
    }

    /**
     * Determina si la plantilla posee un estado dado dentro de su definicion.
     *
     * @param state Estado a buscar.
     * @return Verdadero si contiene el estado dado. Falso en cualquier otro caso.
     */
    Boolean containsState(State state) {
        // Si el estado se encuentra registrado.
        return this.states.contains(state);
    }
}
