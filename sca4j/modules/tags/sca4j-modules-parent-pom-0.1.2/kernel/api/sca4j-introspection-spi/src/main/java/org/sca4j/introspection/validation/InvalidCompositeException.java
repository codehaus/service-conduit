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
package org.sca4j.introspection.validation;

import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import javax.xml.namespace.QName;

import org.sca4j.host.contribution.ValidationFailure;
import org.sca4j.host.contribution.ValidationException;

/**
 * @version $Rev: 5307 $ $Date: 2008-09-01 02:49:55 +0100 (Mon, 01 Sep 2008) $
 */
public class InvalidCompositeException extends ValidationException {
    private static final long serialVersionUID = -2678786389599538999L;

    private final QName name;

    /**
     * Constructor.
     *
     * @param name     the qualified name of the composite that failed validation
     * @param errors   the errors that were found during validation
     * @param warnings the warnings that were found during validation
     */
    public InvalidCompositeException(QName name, List<ValidationFailure> errors, List<ValidationFailure> warnings) {
        super(errors, warnings);
        this.name = name;
    }

    public QName getCompositeName() {
        return name;
    }

    public String getMessage() {
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bas);
        ValidationUtils.writeErrors(writer, getErrors());
        writer.write("\n");
        ValidationUtils.writeWarnings(writer, getWarnings());
        return bas.toString();
    }

}
