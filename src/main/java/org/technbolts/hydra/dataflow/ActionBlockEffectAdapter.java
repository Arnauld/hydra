package org.technbolts.hydra.dataflow;

import fj.Effect;

public class ActionBlockEffectAdapter<T> extends ActionBlock<T> {
    
    private final Effect<T> action;
    
    public ActionBlockEffectAdapter(Effect<T> action) {
        this.action = action;
    }

    public void execute(T message) {
        action.e(message);
    }
}
