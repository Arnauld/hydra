package org.technbolts.hydra.dataflow;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.technbolts.hydra.New;
import org.technbolts.hydra.exception.BlocAlreadyLinkedException;

public class BufferBlock<T> implements SourceBlock<T>, TargetBlock<T> {
    
    public static <T> BufferBlock<T> newBufferBlock(T...values) {
        BufferBlock<T> buffer = new BufferBlock<T>();
        for(T value : values)
            buffer.post(value);
        return buffer;
    }
    
    private ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<T>();
    private AtomicReference<TargetBlock<T>> targetRef = New.atomicReference();
    
    /* (non-Javadoc)
     * @see org.technbolts.hydra.dataflow.TargetBlock#post(java.lang.Object)
     */
    public void post(T message) {
        queue.add(message);
        emitMessages();
    }

    private void emitMessages() {
        TargetBlock<T> targetBlock = targetRef.get();
        if(targetBlock==null)
            return;
        T m;
        while((m=queue.poll())!=null) {
            targetBlock.post(m);
        }
    }

    /* (non-Javadoc)
     * @see org.technbolts.hydra.dataflow.SourceBlock#linkTo(org.technbolts.hydra.dataflow.TargetBlock, boolean)
     */
    public void linkTo(TargetBlock<T> target, boolean unlinkAfterOne) {
        if(!this.targetRef.compareAndSet(null, target)) {
            throw new BlocAlreadyLinkedException();
        }
        emitMessages();
    }
    
}
