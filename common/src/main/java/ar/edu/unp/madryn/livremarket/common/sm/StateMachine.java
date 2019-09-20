package ar.edu.unp.madryn.livremarket.common.sm;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author iMinecrafting
 * Representa la organizacion y funcionamiento de una maquina de estados.
 */
public class StateMachine {
    private List<State> states;
    private List<Transition> transitions;
    @Getter
    private State currentState;
    @Getter
    private List<State> history;

    public StateMachine() {
        this.states = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    /**
     * Agrega un estado a la maquina. Si la maquina aun no tiene estado actual, se setea.
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
        // Si no hay un estado actual aun.
        if(this.currentState == null){
            // Se setea el estado como el actual.
            this.changeCurrentState(state);
        }

        return true;
    }

    /**
     * Agrega una transicion a la maquina de estados.
     * @param transition Transicion a agregar.
     * @return Verdadero si la transicion pudo ser agregada correctamente.
     */
    public Boolean addTransition(Transition transition) {
        // Si la maquina ya contenia la transicion.
        if (this.transitions.contains(transition)) {
            return false;
        }

        // Se agrega la transicion.
        this.transitions.add(transition);

        return true;
    }

    /**
     * Determina si la maquina puede realizar una transicion a partir del estado actual.
     * @return Verdadero si es posible transicionar.
     */
    public Boolean canDoStep() {
        // Si no hay un estado actual.
        if (this.currentState == null) {
            return false;
        }

        // Se evalua si existe una transicion posible partiendo del estado actual.
        return this.hasTransition(this.currentState);
    }

    /**
     * Determina y ejecuta una transicion posible desde el estado actual.
     * @return Verdadero si fue posible realizar una transicion.
     */
    public Boolean doStep() {
        // Si no hay estado actual.
        if (this.currentState == null) {
            return false;
        }

        // Se busca una transicion que parta del estado actual y de la cual se cumpla la condicion.
        Transition transition = this.searchTransition(this.currentState);
        // Si se encontro una transicion.
        if (transition == null) {
            return false;
        }

        // Se agrega el estado actual al historial.
        this.history.add(this.currentState);

        // Se realiza el cambio de estado.
        return this.changeCurrentState(transition.getTo());
    }

    /**
     * Realiza el cambio del estado actual hacia uno especifico.
     * @param state Estado al cual se evoluciona.
     * @return Verdadero si fue posible cambiar de estado.
     */
    private Boolean changeCurrentState(State state) {
        // Si no hay estado.
        if(state == null){
            return false;
        }

        // Si el estado no se encuentra registrado.
        if (!this.states.contains(state)) {
            // Se agrega el estado.
            this.states.add(state);
        }

        // Se cambia el estado actual.
        this.currentState = state;
        // Se ejecuta el proceso del nuevo estado.
        this.currentState.process();

        return true;
    }

    /**
     * Realiza la busqueda de una transicion mediante el estado de origen. En el proceso se evalua si la condicion se cumple.
     * @param from Estado del cual se desea transicionar.
     * @return La transicion encontrada. Si no se encontro una transicion valida, se devuelve null.
     */
    private Transition searchTransition(State from) {
        Optional<Transition> result = this.transitions.stream()
                .filter(transition -> transition.getFrom().equals(from) && transition.condition())
                .findFirst();

        return result.orElse(null);
    }

    /**
     * Determina si hay una transicion que pueda realizarse desde el estado actual. En el proceso se evalua la condicion.
     * @param from Estado del cual se desea transicionar.
     * @return Verdadero si es posible transicionar.
     */
    private Boolean hasTransition(State from) {
        return this.transitions.stream()
                .anyMatch(transition -> transition.getFrom().equals(from) && transition.condition());
    }
}
