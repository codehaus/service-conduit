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

package org.sca4j.ftp.api;

/**
 * @version $Revision$ $Date$
 */
public interface FtpConstants {
    /**
     * Header name for the file content type.
     */
    String HEADER_CONTENT_TYPE = "f3.contentType";
    /**
     * Header value for binary transfers.
     */
    String BINARY_TYPE = "BINARY";
    /**
     * Header value for ASCI transfers.
     */
    String TEXT_TYPE = "TEXT";
    
    /**
     * List of dynamically supplied commands to be executed on the server
     */
    String COMMANDS = "f3.ftp.commands";
}
