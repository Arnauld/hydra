package org.technbolts.hydra.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TaskScheduler {
    
    private final ExecutorService executorService;

    public TaskScheduler(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * @param task
     * @return
     * @see java.util.concurrent.ExecutorService#submit(java.lang.Runnable)
     */
    public <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(task);
    }
    
    

    
}
