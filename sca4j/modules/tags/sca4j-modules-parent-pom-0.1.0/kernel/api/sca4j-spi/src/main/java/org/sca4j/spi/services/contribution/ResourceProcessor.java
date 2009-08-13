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

import java.net.URI;
import java.net.URL;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;

/**
 * Implmentations process a contribution resource for a MIME type.
 *
 * @version $Rev: 4313 $ $Date: 2008-05-24 00:06:47 +0100 (Sat, 24 May 2008) $
 */
public interface ResourceProcessor {

    /**
     * Returns the content type the processor handles
     *
     * @return the content type the processor handles
     */
    String getContentType();

    /**
     * Indexes the resource
     *
     * @param contribution the containing contribution
     * @param url          a dereferenceable url to the resource
     * @param context      the context to which validation errors and warnings are reported
     * @throws ContributionException if an error occurs during indexing
     */
    void index(Contribution contribution, URL url, ValidationContext context) throws ContributionException;

    /**
     * Loads the the Resource
     *
     * @param contributionUri the URI of the active contribution
     * @param resource        the resource to process
     * @param context         the context to which validation errors and warnings are reported
     * @param loader          the classloader contribution the resource must be loaded in @throws ContributionException if an error occurs during
     *                        introspection
     * @throws ContributionException if an error processing the contribution occurs
     */
    void process(URI contributionUri, Resource resource, ValidationContext context, ClassLoader loader) throws ContributionException;

}
