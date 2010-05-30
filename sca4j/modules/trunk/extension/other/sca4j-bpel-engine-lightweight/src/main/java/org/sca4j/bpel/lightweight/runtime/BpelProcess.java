package org.sca4j.bpel.lightweight.runtime;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.sca4j.bpel.lightweight.Sca4jBpelException;
import org.sca4j.bpel.lightweight.model.BpelProcessDefinition;
import org.sca4j.bpel.lightweight.model.VariableDefinition;
import org.sca4j.idl.wsdl.spi.WsdlTypeMapper;

public class BpelProcess {
    
    private Map<String, Object> variableContext = new HashMap<String, Object>();

    public BpelProcess(BpelProcessDefinition processDefinition, WsdlTypeMapper wsdlTypeMapper) {
        initializeVariables(processDefinition, wsdlTypeMapper);
    }

    private void initializeVariables(BpelProcessDefinition processDefinition, WsdlTypeMapper wsdlTypeMapper) {
        for (VariableDefinition variableDefinition : processDefinition.getVariables()) {
            String variableName = variableDefinition.getName();
            QName xmlType = variableDefinition.getType();
            Class<?> javaType = wsdlTypeMapper.get(xmlType);
            try {
            Object instance = javaType.newInstance();
            variableContext.put(variableName, instance);
            } catch (InstantiationException e) {
                throw new Sca4jBpelException("Unable to instantiate variable xml type " + xmlType + " java type " + javaType, e);
            } catch (IllegalAccessException e) {
                throw new Sca4jBpelException("Unable to instantiate variable xml type " + xmlType + " java type " + javaType, e);
            }
        }
    }

}
