package org.technbolts.hydra;

public class Parallel {

    public static ParallelLoopResult ForEach(Runnable...runnables) {
        return For(0, runnables.length, runnables);
    }

    public static ParallelLoopResult For(int fromInclusive, int toExclusive, Runnable...runnables) {
        return For(0, runnables.length, ParallelOptions.getDefaults(), runnables);
    }
    
    public static ParallelLoopResult For(int fromInclusive, int toExclusive, ParallelOptions options, Runnable...runnables) {
        return null;
    }
    
    /**
     * This method can be used to execute a set of operations, potentially in parallel.
     * No guarantees are made about the order in which the operations execute or whether
     * they execute in parallel. This method does not return until each of the provided 
     * operations has completed, regardless of whether completion occurs due to normal 
     * or exceptional termination.
     * @param runnables
     */
    public static void Invoke(Runnable...runnables) {
    }
    
    public static void Invoke(ParallelOptions options, Runnable...runnables) {
    }
    
}
