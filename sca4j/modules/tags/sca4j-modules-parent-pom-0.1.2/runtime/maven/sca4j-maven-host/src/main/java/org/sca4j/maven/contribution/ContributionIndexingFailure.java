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
package org.sca4j.maven.contribution;

import org.sca4j.host.contribution.ValidationFailure;

import java.io.File;

/**
 * Validation warning indicating that the possible contribution file with the given File could not be loaded.
 *
 * @version $Rev$ $Date$
 */
public class ContributionIndexingFailure extends ValidationFailure<File> {
    private Exception ex;

    public ContributionIndexingFailure(File file, Exception ex) {
        super(file);
        this.ex = ex;
    }

    /**
     * Retrieves the message for the failure that includes both the standard ValidationFailure message along with
     * details of the exception.
     * @return the mesasge.
     */
    public String getMessage() {
        return super.getMessage() + " " + ex;
    }
}
