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

import java.net.ConnectException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.OutInAxisOperation;
import org.apache.axis2.description.WSDL2Constants;
import org.osoa.sca.ServiceUnavailableException;
import org.sca4j.binding.ws.axis2.common.Constant;
import org.sca4j.binding.ws.axis2.provision.AxisPolicy;
import org.sca4j.binding.ws.axis2.runtime.config.SCA4JConfigurator;
import org.sca4j.binding.ws.axis2.runtime.policy.PolicyApplier;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.wire.Interceptor;

/**
 * @version $Revision$ $Date$
 */
public class Axis2TargetInterceptor implements Interceptor {

    private Interceptor next;
    private List<String> endpointUris;
    private final String operation;
    private final Set<AxisPolicy> policies;
    private Map<String, String> operationInfo;
    private Map<String, String> config;
    private final SCA4JConfigurator f3Configurator;
    private final PolicyApplier policyApplier;
    private AxisService axisService;

    /**
     * Initializes the end point reference.
     * 
     * @param endpointUri the endpoint uri.
     * @param operation Operation name.
     * @param policies the set of policies applied to the service or reference configuration
     * @param f3Configurator a configuration helper for classloading
     * @param policyApplier the helper for applying configured policies
     */
    public Axis2TargetInterceptor(List<String> endpointUris, 
                                  String operation, 
                                  Set<AxisPolicy> policies,
                                  Map<String, String> operationInfo, 
                                  Map<String, String> config,
                                  SCA4JConfigurator f3Configurator, 
                                  PolicyApplier policyApplier, 
                                  AxisService axisService) {

        this.operation = operation;
        this.endpointUris = endpointUris;
        this.policies = policies;
        this.f3Configurator = f3Configurator;
        this.policyApplier = policyApplier;
        this.operationInfo = operationInfo;
        this.config = config;
        this.axisService = axisService;
        
    }

    public Interceptor getNext() {
        return next;
    }

    public Message invoke(Message msg) {

        Random random = new Random();
        List<String> failedUris = new LinkedList<String>();

        String endpointUri = getEndpointUri(random, failedUris);

        Object[] payload = (Object[]) msg.getBody();
        OMElement message = payload == null ? null : (OMElement) payload[0];

        Options options = new Options();
        options.setTo(new EndpointReference(endpointUri));
        options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
        options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);

        applyOperationInfo(options);
        applyConfig(options);

        Thread currentThread = Thread.currentThread();
        ClassLoader oldCl = currentThread.getContextClassLoader();

        try {

            currentThread.setContextClassLoader(getClass().getClassLoader());

            ServiceClient sender = new ServiceClient(f3Configurator.getConfigurationContext(), null);
            sender.setOptions(options);
            sender.getOptions().setTimeOutInMilliSeconds(0l);
            applyPolicies(sender, operation);

            AxisOperation axisOperation = getAxisOperation(axisService, operation);
            Message ret = new MessageImpl();

            if (WSDL2Constants.MEP_URI_OUT_ONLY.equals(axisOperation.getMessageExchangePattern())
                    || WSDL2Constants.MEP_URI_ROBUST_OUT_ONLY.equals(axisOperation.getMessageExchangePattern())) {
                try {
                    sender.sendRobust(message);
                } catch (AxisFault e) {
                    
                    if (e.getCause() instanceof ConnectException) {
                        throw e; //retry
                    }
                    ret.setBodyWithFault(e.getDetail());
                }

            } else {// Default MEP is IN-OUT
                Object result = sender.sendReceive(message);
                if (result instanceof Throwable) {
                    ret.setBodyWithFault(result);
                } else {
                    ret.setBody(result);
                }
            }

            return ret;

        } catch (AxisFault e) {
            return handleFault(msg, endpointUri, e, random, failedUris);
        } finally {
            currentThread.setContextClassLoader(oldCl);
        }

    }

    private Message handleFault(Message msg, String endpointUri, AxisFault e, Random random, List<String> failedUris) {

        Throwable cause = e.getCause();
        if (cause instanceof ConnectException) {
            failedUris.add(endpointUri);
            if (failedUris.size() != endpointUris.size()) {
                // Retry till all URIs are exhausted
                return invoke(msg);
            }
        }

        SOAPFaultDetail element = e.getFaultDetailElement();
        if (element == null) {
            throw new ServiceUnavailableException("Service fault was: \n" + e + "\n\n", e);
        }

        OMNode child = element.getFirstOMChild();
        if (child == null) {
            throw new ServiceUnavailableException("Service fault was: \n" + e + "\n\n", e);
        }

        throw new ServiceUnavailableException("Service fault was: \n" + child + "\n\n", e);

    }

    private String getEndpointUri(Random random, List<String> failedUris) {

        int index = random.nextInt(endpointUris.size());
        String endpointUri = endpointUris.get(index);

        if (failedUris.contains(endpointUri)) {
            endpointUri = getEndpointUri(random, failedUris);
        }

        return endpointUri;

    }

    private void applyOperationInfo(Options options) {
        String soapAction = "urn:" + operation;// Default

        if (this.operationInfo != null) {
            String soapActionInfo = this.operationInfo.get(Constant.SOAP_ACTION);
            if (soapActionInfo != null) {
                soapAction = soapActionInfo;
            }
        }
        options.setAction(soapAction);
    }

    private void applyConfig(Options options) {
        if (config != null) {
            boolean mtomEnabled = config.get(Constant.CONFIG_ENABLE_MTOM).equalsIgnoreCase(Constant.VALUE_TRUE);
            if (!mtomEnabled) {
                options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_FALSE);
                return;
            }
        }
        // By default MTOM is enabled for backward compatibility.
        options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
    }

    private void applyPolicies(ServiceClient sender, String operation) throws AxisFault {
        if (policies == null) {
            return;
        }

        AxisService axisService = sender.getAxisService();
        AxisOperation axisOperation = axisService.getOperationBySOAPAction("urn:" + operation);
        if (axisOperation == null) {
            axisOperation = axisService.getOperation(ServiceClient.ANON_OUT_IN_OP);
        }
        AxisDescription axisDescription = axisOperation;

        for (AxisPolicy policy : policies) {

            String moduleName = policy.getModule();
            String message = policy.getMessage();

            AxisModule axisModule = f3Configurator.getModule(moduleName);
            axisOperation.addModule(axisModule.getName());
            axisOperation.engageModule(axisModule);

            if (message != null) {
                axisDescription = axisOperation.getMessage(message);
            }
            
            if(policy.getOpaquePolicy() != null) {
                policyApplier.applyPolicy(axisDescription, policy.getOpaquePolicy());
            }
        }
    }

    private AxisOperation getAxisOperation(AxisService axisService, String opName) {
        AxisOperation axisOperation = new OutInAxisOperation();// Default

        if (axisService != null) {
            for (Iterator<?> i = axisService.getOperations(); i.hasNext();) {
                AxisOperation axisOp = (AxisOperation) i.next();
                if (axisOp.getName().getLocalPart().equals(opName)) {
                    axisOperation = axisOp;
                    break;
                }
            }
        }
        return axisOperation;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }
}
