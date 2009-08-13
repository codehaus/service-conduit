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
package org.sca4j.host.domain;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Denotes a recoverable failure updating the domain assembly during deployment. For example, a failure may be a reference targeted to a non-existent
 * service.
 *
 * @version $Revision$ $Date$
 */
public class AssemblyException extends DeploymentException {
    private static final long serialVersionUID = 3957908169593535300L;
    private static final Comparator<AssemblyFailure> COMPARATOR = new Comparator<AssemblyFailure>() {
        public int compare(AssemblyFailure first, AssemblyFailure second) {
            return first.getComponentUri().compareTo(second.getComponentUri());
        }
    };

    private final List<AssemblyFailure> errors;
    private final List<AssemblyFailure> warnings;

    public AssemblyException(List<AssemblyFailure> errors, List<AssemblyFailure> warnings) {
        this.errors = errors;
        this.warnings = warnings;
    }

    public String getMessage() {
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bas);

        if (!errors.isEmpty()) {
            List<AssemblyFailure> sorted = new ArrayList<AssemblyFailure>(errors);
            // sort the errors by component
            Collections.sort(sorted, COMPARATOR);
            for (AssemblyFailure error : sorted) {
                writer.write(error.getMessage());
                writer.write("\n\n");
            }
        }
        if (!warnings.isEmpty()) {
            List<AssemblyFailure> sorted = new ArrayList<AssemblyFailure>(warnings);
            Collections.sort(sorted, COMPARATOR);
            for (AssemblyFailure warning : sorted) {
                writer.write(warning.getMessage());
            }
        }
        writer.flush();
        return bas.toString();

    }
}
