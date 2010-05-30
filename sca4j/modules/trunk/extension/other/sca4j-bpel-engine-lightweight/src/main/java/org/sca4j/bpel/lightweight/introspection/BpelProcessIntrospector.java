package org.sca4j.bpel.lightweight.introspection;

import org.apache.commons.io.IOUtils;
import org.sca4j.bpel.introspection.Constants;
import org.sca4j.bpel.lightweight.Sca4jBpelException;
import org.sca4j.bpel.lightweight.model.*;
import org.sca4j.introspection.xml.LoaderUtil;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

/**
 * Created by IntelliJ IDEA. User: meerajk Date: May 29, 2010 Time: 10:23:43 AM
 * To change this template use File | Settings | File Templates.
 * 
 * TODO Add stricter structural checks
 */
public class BpelProcessIntrospector {

    public BpelProcessDefinition introspect(InputStream inputStream) {

        try {

            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);

            QName elementName = null;
            BpelProcessDefinition bpelProcessDefinition = null;

            while (true) {
                switch (xmlStreamReader.next()) {
                case START_ELEMENT:
                    elementName = xmlStreamReader.getName();
                    if (elementName.equals(Constants.PROCESS_ELEMENT)) {
                        String name = xmlStreamReader.getAttributeValue(null, "elementName");
                        String targetNamespace = xmlStreamReader.getAttributeValue(null, "targetNamespace");
                        QName processName = new QName(targetNamespace, name);
                        bpelProcessDefinition = new BpelProcessDefinition(processName);
                    } else if (elementName.equals(Constants.IMPORT_ELEMENT)) {
                        processImport(xmlStreamReader, bpelProcessDefinition);
                    } else if (elementName.equals(Constants.VARIABLE_ELEMENT)) {
                        processVariable(xmlStreamReader, bpelProcessDefinition);
                    } else if (elementName.equals(Constants.PARTNERLINK_ELEMENT)) {
                        processPartnerLink(xmlStreamReader, bpelProcessDefinition);
                    } else if (elementName.equals(Constants.SEQUENCE_ELEMENT)) {
                        processSequence(bpelProcessDefinition);
                    } else if (elementName.equals(Constants.RECEIVE_ELEMENT)) {
                        processReceive(xmlStreamReader, bpelProcessDefinition);
                    } else if (elementName.equals(Constants.REPLY_ELEMENT)) {
                        processReply(xmlStreamReader, bpelProcessDefinition);
                    } else if (elementName.equals(Constants.INVOKE_ELEMENT)) {
                        processInvoke(xmlStreamReader, bpelProcessDefinition);
                    } else if (elementName.equals(Constants.ASSIGN_ELEMENT)) {
                        processAssign(xmlStreamReader, bpelProcessDefinition);
                    } else if (elementName.equals(Constants.COPY_ELEMENT)) {
                        processCopy(xmlStreamReader, bpelProcessDefinition);
                    } else if (elementName.equals(Constants.FROM_ELEMENT)) {
                        processFrom(xmlStreamReader, bpelProcessDefinition);
                    } else if (elementName.equals(Constants.TO_ELEMENT)) {
                        processTo(xmlStreamReader, bpelProcessDefinition);
                    }
                    break;
                case END_ELEMENT:
                    elementName = xmlStreamReader.getName();
                    if (elementName.equals(Constants.PROCESS_ELEMENT)) {
                        return bpelProcessDefinition;
                    }
                }
            }

        } catch (XMLStreamException e) {
            throw new Sca4jBpelException("Unable tp parse process definition", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }

    private void processSequence(BpelProcessDefinition bpelProcessDefinition) {
        SequenceDefinition sequenceDefinition = new SequenceDefinition();
        bpelProcessDefinition.getSequences().add(sequenceDefinition);
    }

    private void processTo(XMLStreamReader xmlStreamReader, BpelProcessDefinition bpelProcessDefinition) throws XMLStreamException {
        String to = xmlStreamReader.getElementText();
        bpelProcessDefinition.getLastSequence().getLastAssignActivity().getLastCopy().setTo(to);
    }

    private void processFrom(XMLStreamReader xmlStreamReader, BpelProcessDefinition bpelProcessDefinition) throws XMLStreamException {
        String from = xmlStreamReader.getElementText();
        bpelProcessDefinition.getLastSequence().getLastAssignActivity().getLastCopy().setFrom(from);
    }

    private void processAssign(XMLStreamReader xmlStreamReader, BpelProcessDefinition bpelProcessDefinition) {
        AssignDefinition assignDefinition = new AssignDefinition();
        bpelProcessDefinition.getLastSequence().getActivities().add(assignDefinition);
    }

    private void processCopy(XMLStreamReader xmlStreamReader, BpelProcessDefinition bpelProcessDefinition) {
        CopyDefinition copyDefinition = new CopyDefinition();
        bpelProcessDefinition.getLastSequence().getLastAssignActivity().getCopies().add(copyDefinition);
    }

    private void processInvoke(XMLStreamReader xmlStreamReader, BpelProcessDefinition bpelProcessDefinition) {
        String operation = xmlStreamReader.getAttributeValue(null, "operation");
        String partnerLink = xmlStreamReader.getAttributeValue(null, "partnerLink");
        String input = xmlStreamReader.getAttributeValue(null, "inputVariable");
        String output = xmlStreamReader.getAttributeValue(null, "outputVariable");
        InvokeDefinition invokeDefinition = new InvokeDefinition(operation, partnerLink, input, output);
        bpelProcessDefinition.getLastSequence().getActivities().add(invokeDefinition);
    }

    private void processReply(XMLStreamReader xmlStreamReader, BpelProcessDefinition bpelProcessDefinition) {
        String operation = xmlStreamReader.getAttributeValue(null, "operation");
        String partnerLink = xmlStreamReader.getAttributeValue(null, "partnerLink");
        String variable = xmlStreamReader.getAttributeValue(null, "variable");
        ReplyDefinition receiveDefinition = new ReplyDefinition(operation, partnerLink, variable);
        bpelProcessDefinition.getLastSequence().getActivities().add(receiveDefinition);
    }

    private void processReceive(XMLStreamReader xmlStreamReader, BpelProcessDefinition bpelProcessDefinition) {
        String operation = xmlStreamReader.getAttributeValue(null, "operation");
        String partnerLink = xmlStreamReader.getAttributeValue(null, "partnerLink");
        String variable = xmlStreamReader.getAttributeValue(null, "variable");
        ReceiveDefinition receiveDefinition = new ReceiveDefinition(operation, partnerLink, variable);
        bpelProcessDefinition.getLastSequence().getActivities().add(receiveDefinition);
    }

    private void processPartnerLink(XMLStreamReader xmlStreamReader, BpelProcessDefinition bpelProcessDefinition) {
        String name = xmlStreamReader.getAttributeValue(null, "elementName");
        String val = xmlStreamReader.getAttributeValue(null, "partnerLinkType");
        QName partnerLinkType = LoaderUtil.getQName(val, null, xmlStreamReader.getNamespaceContext());
        String myRole = xmlStreamReader.getAttributeValue(null, "myRole");
        String partnerRole = xmlStreamReader.getAttributeValue(null, "partnerRole");
        PartnerLinkDefinition partnerLinkDefinition = new PartnerLinkDefinition(name, partnerLinkType, myRole, partnerRole);
        bpelProcessDefinition.getPartnerLinks().add(partnerLinkDefinition);
    }

    private void processVariable(XMLStreamReader xmlStreamReader, BpelProcessDefinition bpelProcessDefinition) {
        String name = xmlStreamReader.getAttributeValue(null, "elementName");
        String type = xmlStreamReader.getAttributeValue(null, "messageType");
        QName messageType = LoaderUtil.getQName(type, null, xmlStreamReader.getNamespaceContext());
        VariableDefinition variableDefinition = new VariableDefinition(name, messageType);
        bpelProcessDefinition.getVariables().add(variableDefinition);
    }

    private void processImport(XMLStreamReader xmlStreamReader, BpelProcessDefinition bpelProcessDefinition) {
        String location = xmlStreamReader.getAttributeValue(null, "location");
        String importType = xmlStreamReader.getAttributeValue(null, "importType");
        String namespace = xmlStreamReader.getAttributeValue(null, "namespace");
        ImportDefinition importDefinition = new ImportDefinition(location, importType, namespace);
        bpelProcessDefinition.getImports().add(importDefinition);
    }

}
