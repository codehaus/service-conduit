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
package org.sca4j.spi.services.contribution;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;

/**
 * Responsible for handling the specifics of an archive-based contribution.
 *
 * @version $Revision$ $Date$
 */
public interface ArchiveContributionHandler {

    /**
     * Returns the content type of the contribution that this handler can process
     *
     * @return the content type.
     */

    String getContentType();

    /**
     * Returns true if the implementation can process the contribution archive.
     *
     * @param contribution the contribution
     * @return true if the implementation can process the contribution archive
     */
    boolean canProcess(Contribution contribution);

    /**
     * Processes the manifest
     *
     * @param contribution the contribution
     * @param context      the context to which validation errors and warnings are reported
     * @throws ContributionException if an error occurs processing the manifest
     */
    void processManifest(Contribution contribution, ValidationContext context) throws ContributionException;

    /**
     * Iterates through a contribution calling the supplied action when a contained artifact is encountered.
     *
     * @param contribution the contribution
     * @param action       the action to perform when an artifact is encountered
     * @throws ContributionException if an error occurs processing the manifest
     */
    void iterateArtifacts(Contribution contribution, Action action) throws ContributionException;

}
