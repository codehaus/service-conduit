/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without
 * warranties or conditions of any kind.  See the License for the specific language
 * governing permissions and limitations of use of the software. This software is
 * distributed in conjunction with other software licensed under different terms.
 * See the separate licenses for those programs included in the distribution for the
 * permitted and restricted uses of such software.
 */
package org.sca4j.binding.oracle.queue.spi;

/**
 * Exception Used when errors occur within AQ
 */
public class AQQueueException extends Exception {

    private static final long serialVersionUID = -4785489442754279680L;

    /**
     * Default Constructor
     */
    public AQQueueException() {}

    /**
     * Initialised by the error message
     * @param message - the error message for the given exception
     */
    public AQQueueException(String message) {
        super(message);
    }

    /**
     * Initialised by the arror message and the underlying original exception
     * @param message - the error message for the given exception
     * @param orignalException - underlying exception
     */
    public AQQueueException(String message, Throwable originalException) {
        super(message, originalException);
    }

}
