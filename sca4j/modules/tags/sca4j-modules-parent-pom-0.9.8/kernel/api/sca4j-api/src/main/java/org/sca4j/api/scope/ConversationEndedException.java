package org.sca4j.api.scope;

import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Exception thrown to indicate the conversation being used for a stateful interaction has been ended.
 * Note: This was copied from SCA 1.0 spec code (from the osoa namespace) when it was dropped from spec 1.1.
 */
public class ConversationEndedException extends ServiceRuntimeException {
    private static final long serialVersionUID = 3734864942222558406L;

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @see ServiceRuntimeException
     */
    public ConversationEndedException() {
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param message passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public ConversationEndedException(String message) {
        super(message);
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param message passed to ServiceRuntimeException
     * @param cause   passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public ConversationEndedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param cause passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public ConversationEndedException(Throwable cause) {
        super(cause);
    }
}