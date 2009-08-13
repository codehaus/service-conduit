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
package org.sca4j.loader.definitions;

import javax.xml.stream.XMLStreamReader;

import org.sca4j.introspection.xml.XmlValidationFailure;

/**
 * @version $Revision$ $Date$
 */
public class DefinitionProcessingFailure extends XmlValidationFailure<Void> {
    private Throwable cause;

    public DefinitionProcessingFailure(String message, Throwable cause, XMLStreamReader reader) {
        super(message, null, reader);
        this.cause = cause;
    }

    public String getMessage() {
        return super.getMessage() + ". Original error was: \n" + cause;
    }
}
