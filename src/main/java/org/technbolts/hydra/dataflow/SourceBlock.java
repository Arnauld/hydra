package org.technbolts.hydra.dataflow;

public interface SourceBlock<OUT> {
    
    /**
     * @param target
     * @param unlinkAfterOne <code>true</code> if the source should unlink from the
     *      target after successfully propagating a single message; false to remain 
     *      connected even after a single message has been propagated.
     */
    void linkTo(TargetBlock<OUT> target, boolean unlinkAfterOne);
}
