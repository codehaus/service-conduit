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
package org.sca4j.binding.ws.axis2.runtime;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.description.AxisService;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.sca4j.binding.ws.axis2.provision.Axis2WireTargetDefinition;
import org.sca4j.binding.ws.axis2.provision.AxisPolicy;
import org.sca4j.binding.ws.axis2.runtime.config.SCA4JConfigurator;
import org.sca4j.binding.ws.axis2.runtime.policy.PolicyApplier;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.expression.ExpressionExpander;
import org.sca4j.spi.services.expression.ExpressionExpansionException;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * @version $Revision$ $Date$
 *          <p/>
 *
 */
@EagerInit
public class Axis2TargetWireAttacher implements TargetWireAttacher<Axis2WireTargetDefinition> {
    private final PolicyApplier policyApplier;
    private final SCA4JConfigurator f3Configurator;
    private ExpressionExpander expander;
    private ClassLoaderRegistry classLoaderRegistry;

    public Axis2TargetWireAttacher(@Reference PolicyApplier policyApplier,
                                   @Reference SCA4JConfigurator f3Configurator,
                                   @Reference ExpressionExpander expander,
                                   @Reference ClassLoaderRegistry classLoaderRegistry) {
        this.policyApplier = policyApplier;
        this.f3Configurator = f3Configurator;
        this.expander = expander;
        this.classLoaderRegistry = classLoaderRegistry;
    }

    public void attachToTarget(PhysicalWireSourceDefinition source, Axis2WireTargetDefinition target, Wire wire)
            throws WiringException {
        
        List<String> endpointUris = new LinkedList<String>();
        String endpointUri = expandUri(target.getUri());
        StringTokenizer tok = new StringTokenizer(endpointUri);
        while (tok.hasMoreElements()) {
            endpointUris.add(tok.nextToken().trim());
        }
        
        AxisService axisService = createAxisClientService(target);
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {

            String operation = entry.getKey().getName();

            Set<AxisPolicy> policies = target.getPolicies(operation);
            Map<String, String> opInfo = target.getOperationInfo() != null ? target.getOperationInfo().get(operation) : null;
            
            Interceptor interceptor = new Axis2TargetInterceptor(endpointUris, operation, policies, opInfo, target.getConfig(), f3Configurator, policyApplier, axisService);
            entry.getValue().addInterceptor(interceptor);
        }

    }

    public ObjectFactory<?> createObjectFactory(Axis2WireTargetDefinition target) throws WiringException {
        throw new AssertionError();
    }

    /**
     * Expands the target URI if it contains an expression of the form ${..}.
     *
     * @param uri the target uri to expand
     * @return the expanded URI with sourced values for any expressions
     * @throws WiringException if there is an error expanding an expression
     */
    private String expandUri(URI uri) throws WiringException {
        try {
            String decoded = URLDecoder.decode(uri.toASCIIString(), "UTF-8");
            // classloaders not needed since the type is String
            return expander.expand(decoded);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        } catch (ExpressionExpansionException e) {
            throw new WiringException(e);
        }
    }
    
    private URL getWsdlURL(String wsdlLocation, URI classLoaderId) {
        if (wsdlLocation == null) {
            return null;
        }        
        try {
            return new URL(wsdlLocation);
        } catch (MalformedURLException e) {
            return classLoaderRegistry.getClassLoader(classLoaderId).getResource(wsdlLocation);
        }        
    }
    
    /*
     * Create instance of client side Axis2 service to get info about the Webservice
     */
    private AxisService createAxisClientService(Axis2WireTargetDefinition target) throws WiringException{
        
        URL wsdlURL = getWsdlURL(target.getWsdlLocation(), target.getClassloaderURI());    
        if(wsdlURL != null) {
            try {
                return AxisService.createClientSideAxisService(wsdlURL,
                                                               target.getWsdlElement().getServiceName(),
                                                               target.getWsdlElement().getPortName().getLocalPart(),
                                                               new Options());
            } catch (AxisFault e) {
                throw new WiringException(e);
            }
        } else {
            return null;
        }
    }
}
