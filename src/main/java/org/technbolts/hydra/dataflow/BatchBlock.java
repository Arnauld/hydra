package org.technbolts.hydra.dataflow;

import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technbolts.hydra.New;
import org.technbolts.hydra.exception.BlocAlreadyLinkedException;

public class BatchBlock<T> implements SourceBlock<T[]>, TargetBlock<T> {
    
    private static Logger logger = LoggerFactory.getLogger(BatchBlock.class);
    
    /** The lock protecting all mutators */
    private transient final ReentrantLock lock = new ReentrantLock();
    //
    private AtomicReference<TargetBlock<T[]>> targetRef = New.atomicReference();
    //
    private final int batchSize;
    private final Class<T> componentType;
    //
    private volatile int cursor;
    private volatile T[] batchElements;

    /**
     * @param batchSize
     */
    public BatchBlock(int batchSize, Class<T> componentType) {
        super();
        this.batchSize = batchSize;
        this.componentType = componentType;
        this.cursor = 0;
        this.batchElements = newArray();
    }

    @SuppressWarnings("unchecked")
    private T[] newArray() {
        return (T[]) Array.newInstance(componentType, batchSize);
    }

    /* (non-Javadoc)
     * @see org.technbolts.hydra.dataflow.TargetBlock#post(java.lang.Object)
     */
    public void post(T message) {
        T[] toEmit = null;
        
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            batchElements[cursor++] = message;
            if(cursor==batchSize) {
                toEmit = batchElements;
                batchElements = newArray();
                cursor = 0;
            }
        } finally {
            lock.unlock();
        }
        
        if(toEmit!=null) {
            TargetBlock<T[]> targetBlock = targetRef.get();
            if(targetBlock!=null)
                targetBlock.post(toEmit);
            else
                logger.warn("No target linked, batch message lost");
        }
    }

    /* (non-Javadoc)
     * @see org.technbolts.hydra.dataflow.SourceBlock#linkTo(org.technbolts.hydra.dataflow.TargetBlock, boolean)
     */
    public void linkTo(TargetBlock<T[]> target, boolean unlinkAfterOne) {
        if(!this.targetRef.compareAndSet(null, target)) {
            throw new BlocAlreadyLinkedException();
        }
    }

}
