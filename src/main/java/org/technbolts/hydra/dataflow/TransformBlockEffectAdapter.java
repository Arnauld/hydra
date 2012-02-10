package org.technbolts.hydra.dataflow;

import fj.F;

public class TransformBlockEffectAdapter<IN,OUT> extends TransformBlock<IN,OUT> {
    
    private final F<IN,OUT> transformation;

    /**
     * @param transformation
     */
    public TransformBlockEffectAdapter(F<IN, OUT> transformation) {
        super();
        this.transformation = transformation;
    }

    /* (non-Javadoc)
     * @see org.technbolts.hydra.dataflow.TransformBlock#transform(java.lang.Object)
     */
    @Override
    protected OUT transform(IN message) {
        return transformation.f(message);
    }
    
}
