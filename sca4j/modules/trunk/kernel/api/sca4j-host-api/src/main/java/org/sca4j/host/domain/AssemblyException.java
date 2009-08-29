/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
