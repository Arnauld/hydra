package org.technbolts.hydra.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technbolts.hydra.dataflow.Dataflow;

import fj.Effect;
import fj.F;
import fj.F2;

public class Tasks {
    private static Logger logger = LoggerFactory.getLogger(Dataflow.class);

    private static TaskScheduler defaultTaskScheduler;

    public static synchronized TaskScheduler getDefaultTaskScheduler() {
        if (defaultTaskScheduler == null) {
            defaultTaskScheduler = new TaskScheduler(newExecutorService());
        }
        return defaultTaskScheduler;
    }

    protected static ExecutorService newExecutorService() {
        final int nThreads = Math.min(16, Runtime.getRuntime().availableProcessors());
        final ThreadGroup threadGroup = new ThreadGroup("Tasks") {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Uncaught exception for thread [" + t.getName() + "]", e);
            }
        };
        return Executors.newFixedThreadPool(nThreads, new ThreadFactory() {
            private AtomicInteger idGen = new AtomicInteger();

            public Thread newThread(Runnable r) {
                Thread thr = new Thread(threadGroup, r, "Tasks-worker-" + idGen.incrementAndGet());
                thr.setDaemon(true);
                return thr;
            }
        });
    }
    
    public static <ARG, T> TaskEffectAdapter<ARG, T> newTask(F<ARG, T> func) {
        return new TaskEffectAdapter<ARG, T>(func);
    }
    
    public static <ARG, T> TaskEffectAdapter<ARG, T> newTask(String taskName, F<ARG, T> func) {
        return new TaskEffectAdapter<ARG, T>(taskName, func);
    }
    
    public static <ARG, T> TaskEffectAdapter<ARG, T> newTask(F2<TaskEffectAdapter<ARG, T>, ARG, T> func) {
        return new TaskEffectAdapter<ARG, T>(func);
    }
    
    public static <ARG, T> TaskEffectAdapter<ARG, T> newTask(String taskName, F2<TaskEffectAdapter<ARG, T>, ARG, T> func) {
        return new TaskEffectAdapter<ARG, T>(taskName, func);
    }

    
    public static <T> TaskResultCallback<T> effectToCallback(Effect<T> effect) {
        return new TaskResultCallbackEffectAdapter<T>(effect);
    }

}
