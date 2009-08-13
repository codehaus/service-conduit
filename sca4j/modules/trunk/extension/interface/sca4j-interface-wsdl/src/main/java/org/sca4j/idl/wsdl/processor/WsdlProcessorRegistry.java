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
