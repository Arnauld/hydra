package org.technbolts.hydra.dataflow;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technbolts.hydra.New;

public class BroadcastBlock<T> implements SourceBlock<T>, TargetBlock<T> {
    
    private static Logger logger = LoggerFactory.getLogger(BroadcastBlock.class);
    
    private CopyOnWriteArrayList<TargetBlock<T>> targets = New.copyOnWriteArrayList();

    /* (non-Javadoc)
     * @see org.technbolts.hydra.dataflow.TargetBlock#post(java.lang.Object)
     */
    public void post(T message) {
        Iterator<TargetBlock<T>> iterator = targets.iterator();
        if(iterator.hasNext()) {
            do {
                iterator.next().post(message);
            }
            while(iterator.hasNext());
        }
        else {
            logger.warn("No target linked, message lost");
        }
    }

    /* (non-Javadoc)
     * @see org.technbolts.hydra.dataflow.SourceBlock#linkTo(org.technbolts.hydra.dataflow.TargetBlock, boolean)
     */
    public void linkTo(TargetBlock<T> target, boolean unlinkAfterOne) {
        targets.add(target);
    }

}
