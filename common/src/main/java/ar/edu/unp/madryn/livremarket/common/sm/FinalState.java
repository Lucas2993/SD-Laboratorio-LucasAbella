package ar.edu.unp.madryn.livremarket.common.sm;

import java.util.Map;

public class FinalState extends State {
    public FinalState() {
        super(StateMachine.FINAL_STATE_ID);
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        return true;
    }
}
