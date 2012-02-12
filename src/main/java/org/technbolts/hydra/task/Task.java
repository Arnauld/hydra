package org.technbolts.hydra.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technbolts.hydra.New;
import org.technbolts.hydra.exception.InvalidTaskStatusException;
import org.technbolts.hydra.exception.TaskExecutionException;

public abstract class Task<ARG,T> implements Callable<T> {
    
    private static Logger logger = LoggerFactory.getLogger(Task.class);
    private static AtomicLong idGen = new AtomicLong();

    private final TaskScheduler taskScheduler;
    private final String taskName;
    private Future<T> future;
    private Task<T,?> nextTask;
    private ARG parameter;
    private TaskResultCallback<T> resultCallback;
    private ConcurrentLinkedQueue<Task<T,?>> children;
    private volatile AtomicInteger status;

    public Task() {
        this("Task-"+idGen.incrementAndGet(), Tasks.getDefaultTaskScheduler());
    }
    
    public Task(String taskName) {
        this(taskName, Tasks.getDefaultTaskScheduler());
    }
    
    public Task(TaskScheduler taskScheduler) {
        this("Task-"+idGen.incrementAndGet(), taskScheduler);
    }
    
    public Task(String taskName, TaskScheduler taskScheduler) {
        this.taskName = taskName;
        this.taskScheduler = taskScheduler;
        this.status = new AtomicInteger(TaskStatus.Created.ordinal());
        this.children = New.concurrentLinkedQueue();
    }
    
    protected TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }
    
    private void changeStatusOrFail(TaskStatus expect, TaskStatus update) {
        if(!status.compareAndSet(expect.ordinal(), update.ordinal())) {
            throw new InvalidTaskStatusException("Expect: " + expect);
        }
        logger.trace("Task {} status changed to {}", taskName, update);
    }
    
    private void forceStatus(TaskStatus update) {
        status.set(update.ordinal());
        logger.trace("Task {} status changed to {}", taskName, update);
    }
    
    public final T call() {
        try {
            changeStatusOrFail(TaskStatus.WaitingToRun, TaskStatus.Running);
            T result = execute(parameter);
            if(nextTask!=null) {
                logger.trace("Task {} schedules next task {}", taskName, nextTask.taskName);
                nextTask.withParameter(result).start();
            }
            
            // critical section: make sure no children are allowed anymore
            synchronized(status) {
                forceStatus(TaskStatus.SelfRanToCompletion);
            }
            
            if(!children.isEmpty()) {
                int count = 0;
                for(Task<T,?> child : children) {
                    child.withParameter(result).start();
                    count++;
                }
                logger.trace("Task {} scheduled {} children task", taskName, count);
                forceStatus(TaskStatus.WaitingForChildrenToComplete);
            }
            else {
                forceStatus(TaskStatus.RanToCompletion);
            }
            if(resultCallback!=null) {
                resultCallback.onResult(result);
            }
            return result;
        }
        catch(RuntimeException e) {
            forceStatus(TaskStatus.Faulted);
            logger.error("Task " + taskName + "failure", e);
            throw e;
        }
    }
    
    public Task<ARG,T> withParameter(ARG parameter) {
        this.parameter = parameter;
        return this;
    }

    public abstract T execute(ARG parameter);

    public void start() {
        changeStatusOrFail(TaskStatus.Created, TaskStatus.WaitingToRun);
        future = taskScheduler.submit(this);
    }

    public T awaitTermination() throws InterruptedException {
        try {
            // check the parent end first, children are spawn within
            // thus the list can be modified in the meanwhile
            T res = future.get();
            
            if(children!=null) {
                for(Task<T,?> child : children)
                    child.awaitTermination();
            }
            forceStatus(TaskStatus.RanToCompletion);
            return res;
        } catch (InterruptedException e) {
            throw e; 
        } catch (ExecutionException e) {
            throw new TaskExecutionException(e);
        }
    }
    
    public <R,TASK extends Task<T,R>> TASK continueWith(TASK nextTask) {
        this.nextTask = nextTask;
        return nextTask;
    }
    
    public Task<ARG,T> resultCallback(TaskResultCallback<T> resultCallback) {
        this.resultCallback = resultCallback;
        return this;
    }
    
    public <R,TASK extends Task<T,R>> void childTask(TASK child) {
        synchronized(status) {
            if(canAcceptChildren()) {
                this.children.add(child);
            }
        }
    }

    private boolean canAcceptChildren() {
        int ordinal = status.get();
        switch(TaskStatus.valueOf(ordinal)) {
            case Canceled:
            case Faulted:
            case SelfRanToCompletion:
            case WaitingForChildrenToComplete:
            case RanToCompletion:
                return false;
        }
        return true;
    }

}
