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
package org.sca4j.binding.ws.axis2.runtime;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.util.Utils;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.transport.http.AxisServlet;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.ws.axis2.provision.Axis2WireSourceDefinition;
import org.sca4j.binding.ws.axis2.provision.AxisPolicy;
import org.sca4j.binding.ws.axis2.runtime.config.SCA4JConfigurator;
import org.sca4j.binding.ws.axis2.runtime.policy.PolicyApplier;
import org.sca4j.binding.ws.axis2.runtime.servlet.SCA4JAxisServlet;
import org.sca4j.host.runtime.HostInfo;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.host.ServletHost;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;
import org.w3c.dom.Element;

/**
 * @version $Revision$ $Date$
 */
@EagerInit
public class Axis2ServiceProvisionerImpl implements Axis2ServiceProvisioner {

    private final ServletHost servletHost;
    private final ClassLoaderRegistry classLoaderRegistry;
    private final PolicyApplier policyApplier;
    private final SCA4JConfigurator f3Configurator;
    private ServiceProvisionerMonitor monitor;

    private ConfigurationContext configurationContext;
    private String servicePath = "axis2";
    private HostInfo hostInfo;

    public Axis2ServiceProvisionerImpl(@Reference(required = false) ServletHost servletHost, @Reference ClassLoaderRegistry classLoaderRegistry,
            @Reference PolicyApplier policyApplier, @Reference SCA4JConfigurator f3Configurator, @Monitor ServiceProvisionerMonitor monitor, @Reference HostInfo hostInfo) {
        if (servletHost == null) {
            throw new AssertionError("Please configure a servlet host");
        }
        this.servletHost = servletHost;
        this.classLoaderRegistry = classLoaderRegistry;
        this.policyApplier = policyApplier;
        this.f3Configurator = f3Configurator;
        this.monitor = monitor;
        this.hostInfo = hostInfo;
    }

    /**
     * TODO Make configurable: FABRICTHREE-276
     * 
     * @param servicePath Service path for Axis requests.
     */
    @Property
    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }

    /**
     * Initializes the servlet mapping.
     * 
     * @throws Exception If unable to create configuration context.
     */
    @Init
    public void start() throws Exception {

        configurationContext = f3Configurator.getConfigurationContext();

        AxisServlet axisServlet = new SCA4JAxisServlet(configurationContext);
        servletHost.registerMapping("/" + servicePath + "/*", axisServlet);
        monitor.extensionStarted();
    }

    public void provision(Axis2WireSourceDefinition pwsd, Wire wire) throws WiringException {

        try {

            String uri = pwsd.getUri().toASCIIString();
            URI classLoaderUri = pwsd.getClassLoaderId();
            String serviceClass = pwsd.getServiceInterface();

            ClassLoader classLoader = classLoaderRegistry.getClassLoader(classLoaderUri);

            AxisService axisService = new AxisService();

            axisService.setName(uri);
            axisService.setDocumentation("SCA4J enabled axis service");
            axisService.setClientSide(false);
            axisService.setClassLoader(classLoader);
            axisService.setEndpointURL(uri);

            Parameter interfaceParameter = new Parameter(Constants.SERVICE_CLASS, serviceClass);
            axisService.addParameter(interfaceParameter);

            setMessageReceivers(wire, axisService);

            // Reset the name
            axisService.setName(uri);

            configurationContext.getAxisConfiguration().addService(axisService);

            applyPolicies(pwsd, axisService);
            monitor.endpointProvisioned("/" + servicePath + "/" + uri);
        } catch (Exception e) {
            throw new WiringException(e);
        }

    }

    private void applyPolicies(Axis2WireSourceDefinition pwsd, AxisService axisService) throws WiringException, AxisFault {

        if ("true".equals(hostInfo.getProperty("axis2.policy.disable", "false"))) {
            return;
        }

        for (Iterator<?> i = axisService.getOperations(); i.hasNext();) {

            AxisOperation axisOperation = (AxisOperation) i.next();
            String operation = axisOperation.getName().getLocalPart();

            Set<AxisPolicy> policies = pwsd.getPolicies(operation);
            if (policies == null || policies.size() == 0) {
                continue;
            }

            AxisDescription axisDescription = axisOperation;

            for (AxisPolicy axisPolicy : policies) {

                String message = axisPolicy.getMessage();
                String module = axisPolicy.getModule();
                Element opaquePolicy = axisPolicy.getOpaquePolicy();

                AxisModule axisModule = f3Configurator.getModule(module);
                axisOperation.addModule(axisModule.getName());
                axisOperation.engageModule(axisModule);

                if (message != null) {
                    axisDescription = axisOperation.getMessage(message);
                }

                if (opaquePolicy != null) {
                    policyApplier.applyPolicy(axisDescription, opaquePolicy);
                }

            }

        }

    }

    /*
     * Adds the message receivers.
     */
    private void setMessageReceivers(Wire wire, AxisService axisService) throws Exception {

        Map<String, InvocationChain> interceptors = new HashMap<String, InvocationChain>();
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            interceptors.put(entry.getKey().getName(), entry.getValue());
        }

        Utils.fillAxisService(axisService, configurationContext.getAxisConfiguration(), null, null);

        for (Iterator<?> i = axisService.getOperations(); i.hasNext();) {
            AxisOperation axisOp = (AxisOperation) i.next();
            InvocationChain invocationChain = interceptors.get(axisOp.getName().getLocalPart());
            MessageReceiver messageReceiver = null;
            if (WSDL2Constants.MEP_URI_IN_ONLY.equals(axisOp.getMessageExchangePattern()) || WSDL2Constants.MEP_URI_ROBUST_IN_ONLY.equals(axisOp.getMessageExchangePattern())) {
                messageReceiver = new InOnlyServiceProxy(invocationChain);
            } else {// Default MEP is IN-OUT for backward compatibility
                messageReceiver = new InOutServiceProxy(invocationChain);
            }
            axisOp.setMessageReceiver(messageReceiver);
        }

    }

}