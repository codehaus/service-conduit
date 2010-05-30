package org.sca4j.bpel.lightweight.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.jxpath.JXPathContext;
import org.sca4j.bpel.lightweight.Sca4jBpelException;
import org.sca4j.bpel.lightweight.model.AbstractActivity;
import org.sca4j.bpel.lightweight.model.AssignDefinition;
import org.sca4j.bpel.lightweight.model.BpelProcessDefinition;
import org.sca4j.bpel.lightweight.model.CopyDefinition;
import org.sca4j.bpel.lightweight.model.InvokeDefinition;
import org.sca4j.bpel.lightweight.model.ReceiveDefinition;
import org.sca4j.bpel.lightweight.model.ReplyDefinition;
import org.sca4j.bpel.lightweight.model.SequenceDefinition;
import org.sca4j.bpel.lightweight.model.VariableDefinition;
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
        JXPathContext context = JXPathContext.newContext(variableContext);
        
        public AssignActivityExecutor(AssignDefinition assignDefinition) {
            this.assignDefinition = assignDefinition;
        }

        @Override
        public void executeActivity(Message input) {
            for (CopyDefinition copyDefinition : assignDefinition.getCopies()) {
                String from = copyDefinition.getFrom().substring(1); // Strip the leading $
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
            variableContext.put(receiveDefinition.getVariable(), input.getBody());
        }
        
    }
    
    public class ReplyActivityExecutor implements ActivityExecutor {
        
        private ReplyDefinition replyDefinition;
        
        public ReplyActivityExecutor(ReplyDefinition replyDefinition) {
            this.replyDefinition = replyDefinition;
        }

        @Override
        public void executeActivity(Message input) {
            variableContext.put(PROCESS_OUTPUT_VARIABLE, variableContext.get(replyDefinition.getVariable()));
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
            if (output.getBody() != null && outputVariable != null) {
                variableContext.put(outputVariable, output.getBody());
            }
            
        }
        
    }
    
    private class SequenceExecutor {
        
        private List<ActivityExecutor> executors = new LinkedList<ActivityExecutor>();
        
        private Message execute(Message input) {
            for (ActivityExecutor executor : executors) {
                executor.executeActivity(input);
            }
            return new MessageImpl(variableContext.get(PROCESS_OUTPUT_VARIABLE), false, input.getWorkContext());
        }
        
        private boolean match(String partnerLinkName, String operationName) {
            // First activity should be receive
            ReceiveActivityExecutor receiveActivityExecutor = (ReceiveActivityExecutor) executors.get(0);
            return receiveActivityExecutor.match(partnerLinkName, operationName);
        }
        
        private void addActivity(AbstractActivity abstractActivity) {
            switch(abstractActivity.getType()) {
            case ASSIGN:
                executors.add(new AssignActivityExecutor((AssignDefinition) abstractActivity));
                break;
            case REPLY:
                executors.add(new ReplyActivityExecutor((ReplyDefinition) abstractActivity));
                break;
            case RECEIVE:
                executors.add(new ReceiveActivityExecutor((ReceiveDefinition) abstractActivity));
                break;
            case INVOKE:
                executors.add(new InvokeActivityExecutor((InvokeDefinition) abstractActivity));
                break;
            }
        }
        
        
    } 

}
