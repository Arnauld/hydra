package org.technbolts.hydra.task;

public enum TaskStatus {
    /**
     * The task has been initialized but has not yet been scheduled.
     */
    Created,
    /**
     * The task is waiting to be activated and scheduled internally by the infrastructure.
     */
    WaitingForActivation,
    /**
     * The task has been scheduled for execution but has not yet begun executing.
     */
    WaitingToRun,
    /**
     * The task is running but has not yet completed.
     */
    Running,
    /**
     * The task has finished executing and is implicitly waiting for attached child tasks to complete.
     */
    WaitingForChildrenToComplete,
    /**
     * The task completed execution successfully.
     */
    RanToCompletion,
    /**
     * The task itself completed execution successfully, it may still be possible that child tasks
     * are executing or waiting to be scheduled. 
     */
    SelfRanToCompletion,
    /**
     * The task acknowledged cancellation by throwing an OperationCanceledException with its
     * own CancellationToken while the token was in signaled state, or the task's 
     * CancellationToken was already signaled before the task started executing. 
     */
    Canceled,
    /**
     * The task completed due to an unhandled exception.
     */
    Faulted;

    /**
     * @param ordinal
     * @return
     */
    public static TaskStatus valueOf(int ordinal) {
        for(TaskStatus status : values())
            if(status.ordinal() == ordinal)
                return status;
        return null;
    } 
}
