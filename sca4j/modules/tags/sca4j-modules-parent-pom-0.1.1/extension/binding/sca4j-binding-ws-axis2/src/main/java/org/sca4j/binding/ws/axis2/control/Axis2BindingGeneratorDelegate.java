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
package org.sca4j.binding.ws.axis2.control;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.sca4j.binding.ws.axis2.provision.Axis2PolicyAware;
import org.sca4j.binding.ws.axis2.provision.Axis2WireSourceDefinition;
import org.sca4j.binding.ws.axis2.provision.Axis2WireTargetDefinition;
import org.sca4j.binding.ws.axis2.provision.AxisPolicy;
import org.sca4j.binding.ws.provision.WsdlElement;
import org.sca4j.binding.ws.scdl.WsBindingDefinition;
import org.sca4j.host.Namespaces;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.generator.BindingGeneratorDelegate;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.policy.Policy;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @version $Revision$ $Date$
 * 
 * TODO Add support for WSDL Contract
 */
public class Axis2BindingGeneratorDelegate implements BindingGeneratorDelegate<WsBindingDefinition> {
    
    private static final QName POLICY_ELEMENT = new QName(Namespaces.SCA4J_NS, "axisPolicy");    

    public Axis2WireSourceDefinition generateWireSource(LogicalBinding<WsBindingDefinition> binding,
                                                        Policy policy,  
                                                        ServiceDefinition serviceDefinition) throws GenerationException {
        
        Axis2WireSourceDefinition hwsd = new Axis2WireSourceDefinition();
        hwsd.setUri(binding.getBinding().getTargetUri());
        
        ServiceContract<?> contract = serviceDefinition.getServiceContract();
        hwsd.setServiceInterface(contract.getQualifiedInterfaceName());
        
        URI classloaderId = binding.getParent().getParent().getClassLoaderId();
        hwsd.setClassLoaderId(classloaderId);
        
        setPolicyConfigs(hwsd, policy, contract);
        
        return hwsd;
        
    }

    public Axis2WireTargetDefinition generateWireTarget(LogicalBinding<WsBindingDefinition> binding,
                                                        Policy policy,
                                                        ReferenceDefinition referenceDefinition) throws GenerationException {

        Axis2WireTargetDefinition hwtd = new Axis2WireTargetDefinition();
        WsdlElement wsdlElement = parseWsdlElement(binding.getBinding().getWsdlElement());
        hwtd.setWsdlElement(wsdlElement);
        hwtd.setWsdlLocation(binding.getBinding().getWsdlLocation());
        hwtd.setUri(binding.getBinding().getTargetUri());
        
        ServiceContract<?> contract = referenceDefinition.getServiceContract();
        hwtd.setReferenceInterface(contract.getQualifiedInterfaceName());
        
        URI classloaderId = binding.getParent().getParent().getClassLoaderId();
        hwtd.setClassloaderURI(classloaderId);
        
        //Set Axis2 operation parameters
        addOperationInfo(hwtd, contract);
        
        //Set config
        hwtd.setConfig(binding.getBinding().getConfig());
        
        setPolicyConfigs(hwtd, policy, contract);
        
        return hwtd;

    }
    
    private void addOperationInfo(Axis2WireTargetDefinition hwtd, ServiceContract<?> serviceContract) {
    	for (Operation<?> operation : serviceContract.getOperations()) {
            Map<String, String> info = operation.getInfo(org.sca4j.binding.ws.axis2.common.Constant.AXIS2_JAXWS_QNAME);
            if (info != null) {
                hwtd.addOperationInfo(operation.getName(), info);
            }
        }
    }

    private void setPolicyConfigs(Axis2PolicyAware policyAware, Policy policy, ServiceContract<?> serviceContract) throws Axis2GenerationException {
        
        for (Operation<?> operation : serviceContract.getOperations()) {
            
            List<PolicySet> policySets = policy.getProvidedPolicySets(operation);
            if (policySets == null) {
                continue;
            }
            
            for (PolicySet policySet : policy.getProvidedPolicySets(operation)) {
                
                Element policyDefinition = policySet.getExtension();
                QName qname = new QName(policyDefinition.getNamespaceURI(), policyDefinition.getNodeName());
                if (POLICY_ELEMENT.equals(qname)) {
                    throw new Axis2GenerationException("Unknow policy element " + qname);
                }
                
                String module = policyDefinition.getAttribute("module");
                String message = policyDefinition.getAttribute("message");
                Element opaquePolicy = null;
                
                NodeList nodeList = policyDefinition.getChildNodes();
                for (int i = 0;i < nodeList.getLength();i++) {
                    if (nodeList.item(i) instanceof Element) {
                        opaquePolicy = (Element) nodeList.item(i);
                        break;
                    }
                }
                
                AxisPolicy axisPolicy = new AxisPolicy(message, module, opaquePolicy);
                policyAware.addPolicy(operation.getName(), axisPolicy);
                
            }
            
        }
        
    }
    
    private WsdlElement parseWsdlElement(String wsdlElement) throws GenerationException {
        if(wsdlElement == null) {
            return null;
        }
        
        String[] token = wsdlElement.split("#");
        String namespaceUri = token[0];
        
        if (!token[1].startsWith("wsdl.port")) {
            throw new GenerationException("Only WSDL 1.1 ports are currently supported");
        }
        token = token[1].substring(token[1].indexOf('(') + 1, token[1].indexOf(')')).split("/");

        QName serviceName = new QName(namespaceUri, token[0]);
        QName portName = new QName(namespaceUri, token[1]);
        
        return new WsdlElement(serviceName, portName);        
    }

}
