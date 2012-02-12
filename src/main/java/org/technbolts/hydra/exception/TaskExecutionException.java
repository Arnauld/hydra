package org.technbolts.hydra.exception;

@SuppressWarnings("serial")
public class TaskExecutionException extends HydraException {

    public TaskExecutionException() {
        super();
    }

    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskExecutionException(String message) {
        super(message);
    }

    public TaskExecutionException(Throwable cause) {
        super(cause);
    }

}
