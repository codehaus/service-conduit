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
package org.sca4j.idea.run;

import java.net.URL;
import java.util.List;
import javax.xml.namespace.QName;

import org.sca4j.maven.runtime.MavenHostInfo;

/**
 * Host information supplied by IntelliJ
 *
 * @version $Rev: 2290 $ $Date: 2007-12-20 17:33:43 +0000 (Thu, 20 Dec 2007) $
 */
public interface IntelliJHostInfo extends MavenHostInfo {

    /**
     * Returns URL to the current module output directory.
     *
     * @return a URL to the current module output directory.
     */
    URL getOutputDirectory();

    /**
     * Returns URL to the current module test output directory.
     *
     * @return a URL to the current module test output directory
     */
    URL getTestOutputDirectory();

    /**
     * Returns a list of JUnit component implementations to execute.
     *
     * @return a list of JUnit component implementations to execute
     */
    List<String> getJUnitComponentImplementations();

    /**
     * Returns a list of composite QNames to included in the synthetic test composite.
     *
     * @return a list of composite QNames to included in the synthetic test composite
     */
    List<QName> getIncludedComposites();
}
