package org.technbolts.hydra;

import org.technbolts.hydra.task.TaskScheduler;

public class ParallelOptions {

    public static ParallelOptions getDefaults() {
        return null;
    }
    
    /**
     * 
     */
    private final TaskScheduler taskScheduler;
    
    /**
     * 
     */
    private final int maxDegreeOfParallelism;

    /**
     * @param taskScheduler
     * @param maxDegreeOfParallelism
     */
    public ParallelOptions(TaskScheduler taskScheduler, int maxDegreeOfParallelism) {
        this.taskScheduler = taskScheduler;
        this.maxDegreeOfParallelism = maxDegreeOfParallelism;
    }

    /**
     * @return the taskScheduler
     */
    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    /**
     * @return the maxDegreeOfParallelism
     */
    public int getMaxDegreeOfParallelism() {
        return maxDegreeOfParallelism;
    }

}
