package ar.edu.unp.madryn.livremarket.common.sm;

public class FinalState extends State{
    public FinalState() {
        super(StateMachine.FINAL_STATE_ID);
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
