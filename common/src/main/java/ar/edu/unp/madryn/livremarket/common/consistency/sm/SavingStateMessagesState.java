package ar.edu.unp.madryn.livremarket.common.consistency.sm;

import ar.edu.unp.madryn.livremarket.common.sm.State;

import java.util.Map;

public class SavingStateMessagesState extends State {

    public SavingStateMessagesState() {
        super("saving_state_messages");
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
