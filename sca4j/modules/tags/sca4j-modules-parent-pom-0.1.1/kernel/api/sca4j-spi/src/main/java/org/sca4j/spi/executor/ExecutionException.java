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
 *
 */
package org.sca4j.spi.executor;

import org.sca4j.host.SCA4JException;

/**
 * Thrown when an error is encountered executing a Command on a runtime.
 *
 * @version $Rev: 3681 $ $Date: 2008-04-19 19:00:58 +0100 (Sat, 19 Apr 2008) $
 */
public class ExecutionException extends SCA4JException {

	private static final long serialVersionUID = 1302374448438459L;

	/** 
	 * initialize by a given message
	 */
    public ExecutionException(String message) {
    	super(message);
    }

    /** 
	 * initialize by a given message and identifier
	 */
    public ExecutionException(String message, String identifier) {
        super(message, identifier);
    }

    /** 
	 * initialize by a given message, identifier and the underlying cause
	 */
    public ExecutionException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }
    
    /** 
	 * initialize by a given message and underlying cause
	 */
    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
