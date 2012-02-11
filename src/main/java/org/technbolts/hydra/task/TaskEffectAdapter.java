package org.technbolts.hydra.task;

import fj.F;

public class TaskEffectAdapter<ARG,T> extends Task<ARG,T> {
    
    private final F<ARG,T> func;

    public TaskEffectAdapter(F<ARG,T> func) {
        super();
        this.func = func;
    }

    public TaskEffectAdapter(F<ARG,T> func, TaskScheduler taskScheduler) {
        super(taskScheduler);
        this.func = func;
    }
    
    @Override
    public T execute(ARG parameter) {
        return func.f(parameter);
    }
    
    public <N> TaskEffectAdapter<T,N> continueWith(F<T,N> next) {
        TaskEffectAdapter<T,N> nextTask = new TaskEffectAdapter<T,N>(next, getTaskScheduler());
        return continueWith(nextTask);
    }
}
