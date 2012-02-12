package org.technbolts.hydra;

public class CancellationToken {
    /**
     * Gets whether this token is capable of being in the canceled state.
     */
    private final boolean canBeCanceled;
    
    /**
     * Gets whether cancellation has been requested for this token.
     */
    private volatile boolean isCancellationRequested;

    /**
     * @param canBeCanceled
     */
    public CancellationToken(boolean canBeCanceled) {
        super();
        this.canBeCanceled = canBeCanceled;
    }
    
    public void cancel() {
        if(canBeCanceled)
            isCancellationRequested = true;
    }

    /**
     * @return the canBeCanceled
     */
    public boolean canBeCanceled() {
        return canBeCanceled;
    }

    /**
     * @return the isCancellationRequested
     */
    public boolean isCancellationRequested() {
        return isCancellationRequested;
    }
    
    
}
