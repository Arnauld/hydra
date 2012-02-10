package org.technbolts.hydra.exception;

@SuppressWarnings("serial")
public class HydraException extends RuntimeException {

    /**
     * 
     */
    public HydraException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public HydraException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public HydraException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public HydraException(Throwable cause) {
        super(cause);
    }

}
