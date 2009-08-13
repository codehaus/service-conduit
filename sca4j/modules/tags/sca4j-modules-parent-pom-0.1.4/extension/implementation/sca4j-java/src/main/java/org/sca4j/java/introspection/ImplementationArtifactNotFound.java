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
package org.sca4j.java.introspection;

import org.sca4j.host.contribution.ValidationFailure;

/**
 * @version $Revision$ $Date$
 */
public class ImplementationArtifactNotFound extends ValidationFailure<String> {
    private String artifact;

    public ImplementationArtifactNotFound(String implementationClass) {
        super(implementationClass);
    }

    public ImplementationArtifactNotFound(String implementationClass, String artifact) {
        super(implementationClass);
        this.artifact = artifact.replace("/", ".");
    }

    public String getMessage() {
        if (artifact == null || artifact.equals(getValidatable())) {
            return "Implementation class not found: " + getValidatable() + ". Check that the class is contained in the contribution archive, " +
                    "included as a library, or imported in the SCA contribution manifest.";
        } else {
            return "Class " + artifact + " referenced in component implementation " + getValidatable() + " not found. Check that the class is " +
                    "contained in the contribution archive, included as a library, or imported in the SCA contribution manifest.";
        }
    }
}
