package com.travelex.tgbp.transform.api;


/**
 * Exception thrown when a problem occurs during the transformation process.
 */
public class TransformationException extends RuntimeException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -1928080255741051299L;

    /**
     * Initialises the exception message.
     * 
     * @param message Exception message.
     */
    public TransformationException(String message) {
        super(message);
    }

    /**
     * Initialises the root cause.
     * 
     * @param cause Root cause.
     */
    public TransformationException(Throwable cause) {
        super(cause);
    }

    /**
     * Initialises the message and cause.
     * 
     * @param message Exception message.
     * @param cause Root cause.
     */
    public TransformationException(String message, Throwable cause) {
        super(message, cause);
    }

}
