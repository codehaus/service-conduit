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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.spi.host;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

/**
 * Interface implemented by host environments that allow Servlets to be registered.
 * <p/>
 * This interface allows an SCA system service to register a servlet to handle inbound requests.
 *
 * @version $Rev: 3497 $ $Date: 2008-03-31 01:14:05 +0100 (Mon, 31 Mar 2008) $
 */
public interface ServletHost {

    /**
     * Returns the default servlet context associated with the host.
     *
     * @return the default servlet context associated with the host
     */
    ServletContext getServletContext();

    /**
     * Register a mapping for an instance of a Servlet. This requests that the servlet container direct all requests to the designated mapping to the
     * supplied Servlet instance.
     *
     * @param mapping the uri-mapping for the Servlet
     * @param servlet the Servlet that should be invoked
     */
    void registerMapping(String mapping, Servlet servlet);

    /**
     * Unregister a servlet mapping. This directs the servlet contain not to direct any more requests to a previously registered Servlet.
     *
     * @param mapping the uri-mapping for the Servlet
     * @return the servlet that was registered to the mapping, null if nothing was registered to the mapping
     */
    Servlet unregisterMapping(String mapping);

    /**
     * Check to see if a mapping exists.
     *
     * @param mapping the uri-mapping for the Servlet
     * @return true if mapping is registered, false otherwise
     */
    boolean isMappingRegistered(String mapping);


}