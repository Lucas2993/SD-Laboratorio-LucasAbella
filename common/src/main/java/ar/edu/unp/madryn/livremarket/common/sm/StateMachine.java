package ar.edu.unp.madryn.livremarket.common.sm;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author iMinecrafting
 * Representa la organizacion y funcionamiento de una maquina de estados.
 */
public class StateMachine {
    private Template template;
    @Getter
    private State currentState;
    @Getter
    private List<State> history;

    public StateMachine(Template template) {
        this.template = template;
        this.history = new ArrayList<>();
        this.currentState = template.getInitialState();
    }

    public StateMachine(Template template, State initialState) {
        this.template = template;
        this.history = new ArrayList<>();
        this.changeCurrentState(initialState, false);
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
        return this.template.hasTransition(this.currentState);
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
        Transition transition = this.template.searchTransition(this.currentState);
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
        return this.changeCurrentState(state, true);
    }

    /**
     * Realiza el cambio del estado actual hacia uno especifico.
     * @param state Estado al cual se evoluciona.
     * @param withinProcess Define si al cambiar de estado se realiza el proceso.
     * @return Verdadero si fue posible cambiar de estado.
     */
    private Boolean changeCurrentState(State state, boolean withinProcess) {
        // Si no hay estado.
        if(state == null){
            return false;
        }

        // Si el estado no se encuentra registrado.
        if (!this.template.containsState(state)) {
            return false;
        }

        // Se cambia el estado actual.
        this.currentState = state;
        // Si no se desea realizar el proceso al realizar el cambio.
        if(withinProcess) {
            // Se ejecuta el proceso del nuevo estado.
            this.currentState.process();
        }

        return true;
    }
}
