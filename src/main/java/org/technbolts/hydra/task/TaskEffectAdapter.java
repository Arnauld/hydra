package org.technbolts.hydra.task;

import fj.Effect;
import fj.F;
import fj.F2;

public class TaskEffectAdapter<ARG,T> extends Task<ARG,T> {
    
    private final F<ARG,T> func;
    
    public TaskEffectAdapter(F<ARG,T> func) {
        super();
        this.func = func;
    }
    
    public TaskEffectAdapter(String taskName, F<ARG,T> func) {
        super(taskName);
        this.func = func;
    }

    public TaskEffectAdapter(F<ARG,T> func, TaskScheduler taskScheduler) {
        super(taskScheduler);
        this.func = func;
    }
    
    public TaskEffectAdapter(String taskName, F<ARG,T> func, TaskScheduler taskScheduler) {
        super(taskName, taskScheduler);
        this.func = func;
    }
    
    public TaskEffectAdapter(F2<TaskEffectAdapter<ARG,T>, ARG,T> func) {
        super();
        this.func = func.curry().f(this);
    }
    
    public TaskEffectAdapter(String taskName, F2<TaskEffectAdapter<ARG,T>, ARG,T> func) {
        super(taskName);
        this.func = func.curry().f(this);
    }
    
    public TaskEffectAdapter(F2<TaskEffectAdapter<ARG,T>, ARG,T> func, TaskScheduler taskScheduler) {
        super(taskScheduler);
        this.func = func.curry().f(this);
    }
    
    public TaskEffectAdapter(String taskName, F2<TaskEffectAdapter<ARG,T>, ARG,T> func, TaskScheduler taskScheduler) {
        super(taskName, taskScheduler);
        this.func = func.curry().f(this);
    }
    
    @Override
    public T execute(ARG parameter) {
        return func.f(parameter);
    }
    
    public <N> TaskEffectAdapter<T,N> continueWith(F<T,N> next) {
        TaskEffectAdapter<T,N> nextTask = new TaskEffectAdapter<T,N>(next, getTaskScheduler());
        return continueWith(nextTask);
    }
    
    public <N> TaskEffectAdapter<T,N> continueWith(String taskName, F<T,N> next) {
        TaskEffectAdapter<T,N> nextTask = new TaskEffectAdapter<T,N>(taskName, next, getTaskScheduler());
        return continueWith(nextTask);
    }

    
    public TaskEffectAdapter<ARG,T> resultCallback(Effect<T> effect) {
        super.resultCallback(Tasks.effectToCallback(effect));
        return this;
    }
    
    public <N> TaskEffectAdapter<T,N> childTask(F<T,N> func) {
        TaskEffectAdapter<T,N> childTask = new TaskEffectAdapter<T,N>(func, getTaskScheduler());
        childTask(childTask);
        return childTask;
    }
    
    public <N> TaskEffectAdapter<T,N> childTask(String taskName, F<T,N> func) {
        TaskEffectAdapter<T,N> childTask = new TaskEffectAdapter<T,N>(taskName, func, getTaskScheduler());
        childTask(childTask);
        return childTask;
    }

    public <N> TaskEffectAdapter<T,N> childTask(F2<TaskEffectAdapter<T,N>, T, N> func) {
        TaskEffectAdapter<T,N> childTask = new TaskEffectAdapter<T,N>(func, getTaskScheduler());
        childTask(childTask);
        return childTask;
    }
    
    public <N> TaskEffectAdapter<T,N> childTask(String taskName, F2<TaskEffectAdapter<T,N>, T, N> func) {
        TaskEffectAdapter<T,N> childTask = new TaskEffectAdapter<T,N>(taskName, func, getTaskScheduler());
        childTask(childTask);
        return childTask;
    }

}
