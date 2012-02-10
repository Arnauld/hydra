package org.technbolts.hydra.dataflow;

public interface TargetBlock<T> {
    void post(T message);
}
