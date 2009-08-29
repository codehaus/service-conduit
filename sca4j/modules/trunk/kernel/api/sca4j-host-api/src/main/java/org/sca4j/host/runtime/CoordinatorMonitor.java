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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.host.runtime;

import org.sca4j.api.annotation.logging.Info;
import org.sca4j.api.annotation.logging.Severe;

/**
 * Event monitor interface for the bootstrap sequence
 *
 * @version $Rev: 4342 $ $Date: 2008-05-25 19:08:33 +0100 (Sun, 25 May 2008) $
 */
public interface CoordinatorMonitor {

    /**
     * Called when the runtime is initialized.
     *
     * @param message a message
     */
    @Info
    void initialized(String message);

    /**
     * Called when the runtime has joined a domain.
     *
     * @param message a message
     */
    @Info
    void joinedDomain(String message);

    /**
     * Called when the runtime has performed recovery.
     *
     * @param message a message
     */
    @Info
    void recovered(String message);

    /**
     * Called when the runtime has started.
     *
     * @param message a message
     */
    @Info
    void started(String message);

    /**
     * Called when an exception was thrown during a boostrap operation
     *
     * @param e the exception
     */
    @Severe
    void error(Throwable e);

    /**
     * Called when errors are encountered processing policy intents
     *
     * @param description a description of the errors
     */
    @Severe
    void intentErrors(String description);


    /**
     * Called when errors are encountered processing extensions
     *
     * @param description a description of the errors
     */
    @Severe
    void extensionErrors(String description);

}
