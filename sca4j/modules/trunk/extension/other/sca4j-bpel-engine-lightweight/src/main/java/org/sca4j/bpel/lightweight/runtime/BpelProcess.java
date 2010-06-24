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
 */
package org.sca4j.bpel.lightweight.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.jxpath.JXPathContext;
import org.oasisopen.sca.ServiceUnavailableException;
import org.sca4j.bpel.lightweight.Sca4jBpelException;
import org.sca4j.bpel.lightweight.model.AbstractActivity;
import org.sca4j.bpel.lightweight.model.AssignDefinition;
import org.sca4j.bpel.lightweight.model.BpelProcessDefinition;
import org.sca4j.bpel.lightweight.model.CopyDefinition;
import org.sca4j.bpel.lightweight.model.IfDefinition;
import org.sca4j.bpel.lightweight.model.InvokeDefinition;
import org.sca4j.bpel.lightweight.model.ReceiveDefinition;
import org.sca4j.bpel.lightweight.model.ReplyDefinition;
import org.sca4j.bpel.lightweight.model.SequenceDefinition;
import org.sca4j.bpel.lightweight.model.VariableDefinition;
import org.sca4j.bpel.lightweight.model.WhileDefinition;
import org.sca4j.idl.wsdl.spi.WsdlTypeMapper;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;

public class BpelProcess {
    
    private Map<String, Object> variableContext = new HashMap<String, Object>();
    private List<SequenceExecutor> sequenceExecutors = new ArrayList<SequenceExecutor>();
    private Map<String, InvocationChain> invokers = new HashMap<String, InvocationChain>();
    private QName processName;
    private JXPathContext context = JXPathContext.newContext(variableContext);
    
    private static final String PROCESS_OUTPUT_VARIABLE = "process.output.variable";
    
    public BpelProcess(BpelProcessDefinition processDefinition, WsdlTypeMapper wsdlTypeMapper) {
        
        this.processName = processDefinition.getProcessName();
        initializeVariables(processDefinition, wsdlTypeMapper);
        
        for (SequenceDefinition sequenceDefinition : processDefinition.getSequences()) {
            SequenceExecutor sequenceExecutor = new SequenceExecutor();
            for (AbstractActivity abstractActivity : sequenceDefinition.getActivities()) {
                sequenceExecutor.addActivity(abstractActivity);
            }
            sequenceExecutors.add(sequenceExecutor);
        }
        
        for (Map.Entry<String, InvocationChain> entry : processDefinition.getInvocationChains().entrySet()) {
            invokers.put(entry.getKey(), entry.getValue());
        }
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
                // throw new Sca4jBpelException("Unable to instantiate variable xml type " + xmlType + " java type " + javaType, e);
                // TODO MKU type cannot be instantiated, log warning
            } catch (IllegalAccessException e) {
                throw new Sca4jBpelException("Unable to instantiate variable xml type " + xmlType + " java type " + javaType, e);
            }
        }
    }

    public Message invoke(String partnerLinkName, String operationName, Message message) {
        for (SequenceExecutor sequenceExecutor : sequenceExecutors) {
            if (sequenceExecutor.match(partnerLinkName, operationName)) {
                return sequenceExecutor.execute(message);
            }
        }
        throw new Sca4jBpelException("No sequence found in the process " + processName + " for partner link " + partnerLinkName + " and operation " + operationName);
    }
    
    private interface ActivityExecutor {
        void executeActivity(Message input);
    }
    
    public class AssignActivityExecutor implements ActivityExecutor {
        
        private AssignDefinition assignDefinition;
        
        public AssignActivityExecutor(AssignDefinition assignDefinition) {
            this.assignDefinition = assignDefinition;
        }

        @Override
        public void executeActivity(Message input) {
            for (CopyDefinition copyDefinition : assignDefinition.getCopies()) {
                String from = copyDefinition.getFrom();
                if (from.startsWith("$")) {
                    from = from.substring(1); // Strip the leading $
                }
                String to = copyDefinition.getTo().substring(1); // Strip the leading $
                Object value = context.getValue(from);
                context.setValue(to, value);
            }
        }
        
    }
    
    public class ReceiveActivityExecutor implements ActivityExecutor {
        
        private ReceiveDefinition receiveDefinition;
        
        private ReceiveActivityExecutor(ReceiveDefinition receiveDefinition) {
            this.receiveDefinition = receiveDefinition;
        }
        
        public boolean match(String partnerLinkName, String operationName) {
            return partnerLinkName.equals(receiveDefinition.getPartnerLink()) && operationName.equals(receiveDefinition.getOperation());
        }
        
        @Override
        public void executeActivity(Message input) {
            if (input.getBody() instanceof Object[]) {
                variableContext.put(receiveDefinition.getVariable(), ((Object[]) input.getBody())[0]);
            } else {
                variableContext.put(receiveDefinition.getVariable(), input.getBody());
            }
        }
        
    }
    
    public class ReplyActivityExecutor implements ActivityExecutor {
        
        private ReplyDefinition replyDefinition;
        
        public ReplyActivityExecutor(ReplyDefinition replyDefinition) {
            this.replyDefinition = replyDefinition;
        }

        @Override
        public void executeActivity(Message input) {
            if (replyDefinition.getVariable() != null) {
                variableContext.put(PROCESS_OUTPUT_VARIABLE, variableContext.get(replyDefinition.getVariable()));
            } else {
                variableContext.put(PROCESS_OUTPUT_VARIABLE, null);
            }
        }
        
    }
    
    public class InvokeActivityExecutor implements ActivityExecutor {
        
        private InvokeDefinition invokeDefinition;        
        
        public InvokeActivityExecutor(InvokeDefinition invokeDefinition) {
            this.invokeDefinition = invokeDefinition;
        }

        @Override
        public void executeActivity(Message input) {
            
            String partnerLink = invokeDefinition.getPartnerLink();
            String operation = invokeDefinition.getOperation();
            String inputVariable = invokeDefinition.getInput();
            String outputVariable = invokeDefinition.getOutput();
            Object param = inputVariable == null ? null : variableContext.get(inputVariable);
            
            Interceptor interceptor = invokers.get(partnerLink + "/" + operation).getHeadInterceptor();
            Message output = interceptor.invoke(new MessageImpl(new Object[] {param}, false, input.getWorkContext()));
            if (output.isFault()) {
                throw new ServiceUnavailableException((Throwable) output.getBody());
            }
            if (output.getBody() != null && outputVariable != null) {
                variableContext.put(outputVariable, output.getBody());
            }
            
        }
        
    }
    
    public class IfActivityExecutor implements ActivityExecutor {
        
        private IfDefinition ifDefinition;   
        
        public IfActivityExecutor(IfDefinition ifDefinition) {
            this.ifDefinition = ifDefinition;
        }

        @Override
        public void executeActivity(Message input) {
            
            if (evaluate()) {
                for (AbstractActivity abstractActivity : ifDefinition.getActions()) {
                    getExecutor(abstractActivity).executeActivity(input);
                }
                return;
            }
            for (IfDefinition elseIfDefinition : ifDefinition.getElseIfs()) {
                IfActivityExecutor elseIfExecutor = new IfActivityExecutor(elseIfDefinition);
                if (elseIfExecutor.evaluate()) {
                    elseIfExecutor.executeActivity(input);
                    return;
                }
            }
            for (AbstractActivity abstractActivity : ifDefinition.getElseActivities()) {
                getExecutor(abstractActivity).executeActivity(input);
            }
            
        }
        
        private boolean evaluate() {
            String condition = ifDefinition.getCondition().substring(1); // Strip the leading $
            return (Boolean) context.getValue(condition, Boolean.class);
        }
        
    }
    
    public class WhileActivityExecutor implements ActivityExecutor {
        
        private WhileDefinition whileDefinition;   
        
        public WhileActivityExecutor(WhileDefinition whileDefinition) {
            this.whileDefinition = whileDefinition;
        }

        @Override
        public void executeActivity(Message input) {
            
            while (evaluate()) {
                for (AbstractActivity abstractActivity : whileDefinition.getActions()) {
                    getExecutor(abstractActivity).executeActivity(input);
                    if (variableContext.containsKey(PROCESS_OUTPUT_VARIABLE)) {
                        return;
                    }
                }
            }
            return;
            
        }
        
        private boolean evaluate() {
            String condition = whileDefinition.getCondition().substring(1); // Strip the leading $
            return (Boolean) context.getValue(condition, Boolean.class);
        }
        
    }
    
    private class SequenceExecutor {
        
        private List<ActivityExecutor> executors = new LinkedList<ActivityExecutor>();
        
        private Message execute(Message input) {
            for (ActivityExecutor executor : executors) {
                executor.executeActivity(input);
                if (variableContext.containsKey(PROCESS_OUTPUT_VARIABLE)) {
                    // A reply has been executed
                    return new MessageImpl(variableContext.get(PROCESS_OUTPUT_VARIABLE), false, input.getWorkContext());
                }
            }
            return new MessageImpl(null, false, input.getWorkContext());
        }
        
        private boolean match(String partnerLinkName, String operationName) {
            // First activity should be receive
            ReceiveActivityExecutor receiveActivityExecutor = (ReceiveActivityExecutor) executors.get(0);
            return receiveActivityExecutor.match(partnerLinkName, operationName);
        }
        
        private void addActivity(AbstractActivity abstractActivity) {
            ActivityExecutor activityExecutor = getExecutor(abstractActivity);
            executors.add(activityExecutor);
        }
        
        
    } 
    
    private ActivityExecutor getExecutor(AbstractActivity abstractActivity) {
        switch(abstractActivity.getType()) {
        case ASSIGN:
            return new AssignActivityExecutor((AssignDefinition) abstractActivity);
        case REPLY:
            return new ReplyActivityExecutor((ReplyDefinition) abstractActivity);
        case RECEIVE:
            return new ReceiveActivityExecutor((ReceiveDefinition) abstractActivity);
        case INVOKE:
            return new InvokeActivityExecutor((InvokeDefinition) abstractActivity);
        case IF:
            return new IfActivityExecutor((IfDefinition) abstractActivity);
        case WHILE:
            return new WhileActivityExecutor((WhileDefinition) abstractActivity);
        default:
            throw new IllegalArgumentException("Unknown activity type " + abstractActivity.getType());
        }
    }

}
