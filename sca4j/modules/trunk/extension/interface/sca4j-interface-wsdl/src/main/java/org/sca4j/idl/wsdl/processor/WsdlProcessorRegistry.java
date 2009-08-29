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
package org.sca4j.idl.wsdl.processor;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaType;
import org.sca4j.idl.wsdl.version.WsdlVersionChecker;
import org.sca4j.idl.wsdl.version.WsdlVersionChecker.WsdlVersion;
import org.sca4j.scdl.Operation;

/**
 * Default WSDL processor implementation.
 *
 * @version $Revsion$ $Date: 2008-02-06 18:08:27 +0000 (Wed, 06 Feb 2008) $
 */
public class WsdlProcessorRegistry implements WsdlProcessor {

    /**
     * WSDL processors.
     */
    private Map<WsdlVersion, WsdlProcessor> wsdlProcessors = new HashMap<WsdlVersion, WsdlProcessor>();
    
    /**
     * WSDL version checker.
     */
    private WsdlVersionChecker versionChecker;

    /**
     * @param versionChecker Injected WSDL version checker.
     */
    public WsdlProcessorRegistry(WsdlVersionChecker versionChecker) {
        this.versionChecker = versionChecker;
    }

    public List<Operation<XmlSchemaType>> getOperations(QName portTypeOrInterfaceName, URL wsdlUrl) {

        WsdlVersion wsdlVersion = versionChecker.getVersion(wsdlUrl);
        if(!wsdlProcessors.containsKey(wsdlVersion)) {
            throw new WsdlProcessorException("No processor registered for version " + wsdlVersion);
        }
        return wsdlProcessors.get(wsdlVersion).getOperations(portTypeOrInterfaceName, wsdlUrl);

    }

    /**
     * Registers a processor.
     *
     * @param wsdlVersion WSDL version.
     * @param processor WSDL processor.
     */
    public void registerProcessor(WsdlVersion wsdlVersion, WsdlProcessor processor) {
        wsdlProcessors.put(wsdlVersion, processor);
    }

}
