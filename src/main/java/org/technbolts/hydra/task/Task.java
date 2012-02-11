package org.technbolts.hydra.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.technbolts.hydra.exception.TaskExecutionException;

public abstract class Task<ARG,T> implements Callable<T> {

    private final TaskScheduler taskScheduler;
    private Future<T> future;
    private Task<T,?> nextTask;
    private ARG parameter;

    public Task() {
        this(Tasks.getDefaultTaskScheduler());
    }
    
    public Task(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }
    
    protected TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }
    
    public final T call() {
        T result = execute(parameter);
        if(nextTask!=null) {
            nextTask.withParameter(result).start();
        }
        return result;
    }

    public Task<ARG,T> withParameter(ARG parameter) {
        this.parameter = parameter;
        return this;
    }

    public abstract T execute(ARG parameter);

    public void start() {
        future = taskScheduler.submit(this);
    }

    public T awaitTermination() throws InterruptedException {
        try {
            return future.get();
        } catch (InterruptedException e) {
            throw e; 
        } catch (ExecutionException e) {
            throw new TaskExecutionException(e.getCause());
        }
    }
    
    public <R,TT extends Task<T,R>> TT continueWith(TT nextTask) {
        this.nextTask = nextTask;
        return nextTask;
    }
}
