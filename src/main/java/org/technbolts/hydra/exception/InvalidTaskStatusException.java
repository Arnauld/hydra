package org.technbolts.hydra.exception;

@SuppressWarnings("serial")
public class InvalidTaskStatusException extends HydraException {

    public InvalidTaskStatusException() {
        super();
    }

    public InvalidTaskStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTaskStatusException(String message) {
        super(message);
    }

    public InvalidTaskStatusException(Throwable cause) {
        super(cause);
    }

}
