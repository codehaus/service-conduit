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
package org.sca4j.spi.services.synthesize;

import java.util.List;

import org.sca4j.host.runtime.InitializationException;
import org.sca4j.host.contribution.ValidationFailure;
import org.sca4j.introspection.xml.XmlValidationFailure;

/**
 * @version $Rev: 5330 $ $Date: 2008-09-05 08:28:23 +0100 (Fri, 05 Sep 2008) $
 */
public class InvalidServiceContractException extends InitializationException {
    private static final long serialVersionUID = 4367622270403828483L;
    private List<ValidationFailure> errors;

    public InvalidServiceContractException(List<ValidationFailure> errors) {
        super("System service contract has errors");
        this.errors = errors;
    }

    public String getMessage() {
        if (errors == null) {
            return super.getMessage();
        }
        StringBuilder b = new StringBuilder();
        if (errors.size() == 1) {
            b.append("1 error was detected: \n");
        } else {
            b.append(errors.size()).append(" errors were detected: \n");
        }
        for (ValidationFailure failure : errors) {
            if (failure instanceof XmlValidationFailure) {
                b.append("ERROR: ").append(failure.getMessage()).append("\n");
            } else {
                b.append("ERROR: ").append(failure);
            }
            b.append("\n");
        }
        return b.toString();
    }


}
