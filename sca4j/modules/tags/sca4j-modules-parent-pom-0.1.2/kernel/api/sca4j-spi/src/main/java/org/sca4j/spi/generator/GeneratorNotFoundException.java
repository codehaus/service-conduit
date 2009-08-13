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
package org.sca4j.spi.generator;

import javax.xml.namespace.QName;


/**
 * @version $Rev: 3678 $ $Date: 2008-04-19 18:05:57 +0100 (Sat, 19 Apr 2008) $
 */
public class GeneratorNotFoundException extends GenerationException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -4738988978020234242L;

    /**
     * Initializes the message.
     *
     * @param type Type for which generator was not found.
     */
    public GeneratorNotFoundException(Class<?> type) {
        super("Generator not registered for type: " + type.getName(), type.getName());
    }

    /**
     * Initializes the message.
     *
     * @param type Type for which generator was not found.
     */
    public GeneratorNotFoundException(QName type) {
        super("Generator not registered for type: " + type.toString(), type.toString());
    }

}
