package org.technbolts.hydra.dataflow;

import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technbolts.hydra.New;
import org.technbolts.hydra.exception.BlocAlreadyLinkedException;

public abstract class TransformBlock<IN, OUT> implements SourceBlock<OUT>, TargetBlock<IN> {
    
    private static Logger logger = LoggerFactory.getLogger(TransformBlock.class);
    
    private AtomicReference<TargetBlock<OUT>> targetRef = New.atomicReference();

    /* (non-Javadoc)
     * @see org.technbolts.hydra.dataflow.TargetBlock#post(java.lang.Object)
     */
    public void post(IN message) {
        TargetBlock<OUT> targetBlock = targetRef.get();
        if(targetBlock!=null) {
            OUT transformed = transform(message);
            targetBlock.post(transformed);
        }
        else
            logger.warn("No target linked, message lost");
    }

    /**
     * @param message
     * @return
     */
    protected abstract OUT transform(IN message);

    /* (non-Javadoc)
     * @see org.technbolts.hydra.dataflow.SourceBlock#linkTo(org.technbolts.hydra.dataflow.TargetBlock, boolean)
     */
    public void linkTo(TargetBlock<OUT> target, boolean unlinkAfterOne) {
        if(!this.targetRef.compareAndSet(null, target)) {
            throw new BlocAlreadyLinkedException();
        }
    }
    
}
