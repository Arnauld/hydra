package org.technbolts.hydra.dataflow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dataflow {
    
    private static Logger logger = LoggerFactory.getLogger(Dataflow.class);
    
    private static Dataflow dataflow = new Dataflow();
    public static Dataflow get() {
        return dataflow;
    }
    
    private ExecutorService executorService;
    private int nThreads;
    private ThreadGroup threadGroup;
    private Dataflow() {
    }
    
    public synchronized ExecutorService getExecutorService() {
        if(executorService==null) {
            nThreads = Math.min(16, Runtime.getRuntime().availableProcessors());
            threadGroup = new ThreadGroup("Dataflow") {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    logger.error("Uncaught exception for thread [" + t.getName() + "]", e);
                }
            };
            executorService = Executors.newFixedThreadPool(nThreads, new ThreadFactory() {
                private AtomicInteger idGen = new AtomicInteger();
                public Thread newThread(Runnable r) {
                    Thread thr = new Thread(threadGroup, r, "Dataflow-worker-" + idGen.incrementAndGet());
                    thr.setDaemon(true);
                    return thr;
                }
            });
        }
        return executorService;
    }
    
    public static <T> AsyncTargetBlock<T> makeAsync(final TargetBlock<T> targetBlock, final BlockExecutor executor) {
        return new AsyncTargetBlock<T>(targetBlock, executor);
    }
    
    public static <T> Runnable asRunnable(final TargetBlock<T> targetBlock, final T arg) {
        return new RunnableTargetBlock<T>(targetBlock, arg);
    }

    public static <T> void post(TargetBlock<T> target, T...values) {
        for(T value : values) {
            target.post(value);
        }
    }

    public static <T> ActionBlockCollector<T> collectorActionBlock() {
        return new ActionBlockCollector<T>();
    }
}
