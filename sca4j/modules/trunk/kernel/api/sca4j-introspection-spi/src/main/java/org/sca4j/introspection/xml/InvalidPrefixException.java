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
package org.sca4j.introspection.xml;

import javax.xml.stream.XMLStreamReader;

/**
 * Denotes an invalid QName prefix.
 *
 * @version $Rev: 4301 $ $Date: 2008-05-23 06:33:58 +0100 (Fri, 23 May 2008) $
 */
public class InvalidPrefixException extends LoaderException {
    private static final long serialVersionUID = -4896928793798546890L;
    private String prefix;

    public InvalidPrefixException(String message, String prefix, XMLStreamReader reader) {
        super(message, reader);
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
