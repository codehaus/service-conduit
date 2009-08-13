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
package org.sca4j.spi.services.contribution;

/**
 * @version $Rev: 2383 $ $Date: 2007-12-28 10:01:52 +0000 (Fri, 28 Dec 2007) $
 */
public final class ContributionConstants {
    /**
     * The name and location of the sca metadata artifact
     */
    public static final String SCA_CONTRIBUTION_META = "META-INF/sca-contribution.xml";

    /**
     * The name and location of the generated sca metadata artifact
     */
    public static final String SCA_CONTRIBUTION_GENERATED_META = "META-INF/sca-contribution-generated.xml";

    private ContributionConstants() {
    }
}
