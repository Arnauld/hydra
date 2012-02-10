package org.technbolts.hydra.dataflow;


public class AsyncTargetBlock<T> implements TargetBlock<T> {
    
    private final TargetBlock<T> underlying;
    private final BlockExecutor executor;
    
    /**
     * @param underlying
     * @param executor
     */
    public AsyncTargetBlock(TargetBlock<T> underlying, BlockExecutor executor) {
        super();
        this.underlying = underlying;
        this.executor = executor;
    }

    /* (non-Javadoc)
     * @see org.technbolts.hydra.dataflow.TargetBlock#post(java.lang.Object)
     */
    public void post(T message) {
        executor.execute(Dataflow.asRunnable(underlying, message));
    }
    
}
