package org.technbolts.hydra.dataflow;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technbolts.hydra.New;
import org.technbolts.hydra.Objects;
import org.technbolts.hydra.exception.BlocAlreadyLinkedException;

public abstract class JoinBlock<IN1,IN2,OUT> {
    
    private static Logger logger = LoggerFactory.getLogger(JoinBlock.class);
    
    private ConcurrentLinkedQueue<Tuple<IN1,IN2>> queue = New.concurrentLinkedQueue();
    private AtomicReference<TargetBlock<OUT>> targetRef = New.atomicReference();
    
    protected abstract OUT assemble(IN1 in1, IN2 in2);

    /* (non-Javadoc)
     * @see org.technbolts.hydra.dataflow.SourceBlock#linkTo(org.technbolts.hydra.dataflow.TargetBlock, boolean)
     */
    public void linkTo(TargetBlock<OUT> target, boolean unlinkAfterOne) {
        if(!this.targetRef.compareAndSet(null, target)) {
            throw new BlocAlreadyLinkedException();
        }
        emitComplete();
    }
    
    private TargetBlock<IN1> target1;
    private TargetBlock<IN2> target2;
    
    public TargetBlock<IN1> asTarget1() {
        if(target1==null) {
            target1 = new TargetBlock<IN1>() {
                public void post(IN1 message) {
                    logger.trace("Target1 received one message <{}>", message);
                    assign1(message);
                }
            };
        }
        return target1;
    }
    
    public TargetBlock<IN2> asTarget2() {
        if(target2==null) {
            target2 = new TargetBlock<IN2>() {
                public void post(IN2 message) {
                    logger.trace("Target2 received one message <{}>", message);
                    assign2(message);
                }
            };
        }
        return target2;
    }
    
    protected void emitComplete() {
        TargetBlock<OUT> targetBlock = targetRef.get();
        if(targetBlock==null)
            return;
        Iterator<Tuple<IN1, IN2>> iterator = queue.iterator();
        while(iterator.hasNext()) {
            Tuple<IN1, IN2> next = iterator.next();
            logger.trace("Checking tuple ({},{})", Objects.o(next.t1.get(), next.t2.get()));
            if(next.isComplete()) {
                iterator.remove();
                OUT transformed = assemble(next.t1.get(), next.t2.get());
                logger.trace("Tuple complete and assembled into <{}>", transformed);
                targetBlock.post(transformed);
            }
            else return;
        }
    }

    protected void assign1(IN1 message) {
        for(Tuple<IN1,IN2> t : queue) {
            if(t.assign1IfEmpty(message)) {
                emitComplete();
                return;
            }
        }
        queue.add(new Tuple<IN1,IN2>(message, null));
    }

    protected void assign2(IN2 message) {
        for(Tuple<IN1,IN2> t : queue) {
            if(t.assign2IfEmpty(message)) {
                emitComplete();
                return;
            }
        }
        queue.add(new Tuple<IN1,IN2>(null, message));
    }

    private static class Tuple<IN1,IN2> {
        private AtomicReference<IN1> t1;
        private AtomicReference<IN2> t2;
        public Tuple(IN1 t1, IN2 t2) {
            this.t1 = New.atomicReference(t1);
            this.t2 = New.atomicReference(t2);
        }
        public boolean isComplete() {
            return (t1.get()!=null && t2.get()!=null);
        }
        public boolean assign1IfEmpty(IN1 in1) {
            return t1.compareAndSet(null, in1);
        }
        public boolean assign2IfEmpty(IN2 in2) {
            return t2.compareAndSet(null, in2);
        }
    }

}
