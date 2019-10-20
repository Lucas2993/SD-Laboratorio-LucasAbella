package ar.edu.unp.madryn.livremarket.common.sm;

public class InitialState extends State {
    public InitialState() {
        super(StateMachine.INITIAL_STATE_ID);
    }

    @Override
    public Boolean reset() {
        return true;
    }

    @Override
    public Boolean process() {
        return true;
    }
}
