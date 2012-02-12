package org.technbolts.hydra.task;

public interface TaskResultCallback<T> {
    void onResult(T result);
}
