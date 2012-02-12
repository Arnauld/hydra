package org.technbolts.hydra.task;

import fj.Effect;

public class TaskResultCallbackEffectAdapter<T> implements TaskResultCallback<T> {
    
    private final Effect<T> effect;
    
    public TaskResultCallbackEffectAdapter(Effect<T> effect) {
        super();
        this.effect = effect;
    }
    
    public void onResult(T result) {
        effect.e(result);
    }

}
