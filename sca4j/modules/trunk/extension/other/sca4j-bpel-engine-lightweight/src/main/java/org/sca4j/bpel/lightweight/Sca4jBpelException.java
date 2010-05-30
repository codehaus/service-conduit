package org.sca4j.bpel.lightweight;

import org.sca4j.host.SCA4JRuntimeException;

/**
 * Created by IntelliJ IDEA. User: meerajk Date: May 29, 2010 Time: 10:25:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class Sca4jBpelException extends SCA4JRuntimeException {

    /**
     * 
     * @param message
     *            Message for the exception.
     * @param cause
     *            Root cause for the exception.
     */
    public Sca4jBpelException(String message, Throwable cause) {
        super(message, cause);
    }

    public Sca4jBpelException(String message) {
        super(message);
    }
}
