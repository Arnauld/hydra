package org.technbolts.hydra.dataflow;

public class RunnableTargetBlock<T> implements Runnable {

    private final TargetBlock<T> underling;
    private final T message;
    /**
     * @param underling
     * @param message
     */
    public RunnableTargetBlock(TargetBlock<T> underling, T message) {
        super();
        this.underling = underling;
        this.message = message;
    }
    
    public void run() {
        underling.post(message);
    }
}
