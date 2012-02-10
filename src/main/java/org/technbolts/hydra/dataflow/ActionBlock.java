package org.technbolts.hydra.dataflow;

public abstract class ActionBlock<T> implements TargetBlock<T> {
    
    public void post(T message) {
        execute(message);
    }

    public abstract void execute(T message);
    
}
