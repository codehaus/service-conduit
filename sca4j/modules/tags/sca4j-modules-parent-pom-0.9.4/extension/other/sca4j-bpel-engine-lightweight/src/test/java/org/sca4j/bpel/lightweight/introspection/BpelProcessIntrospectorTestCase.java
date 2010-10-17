package org.sca4j.bpel.lightweight.introspection;

import junit.framework.TestCase;
import org.sca4j.bpel.lightweight.model.*;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: meerajk
 * Date: May 29, 2010
 * Time: 12:48:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class BpelProcessIntrospectorTestCase extends TestCase {

    public void testIntrospect() throws Exception {

        BpelProcessIntrospector bpelProcessIntrospector = new BpelProcessIntrospector();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("order.bpel");
        BpelProcessDefinition bpelProcessDefinition = bpelProcessIntrospector.introspect(inputStream);

        assertNotNull(bpelProcessDefinition);
        assertEquals(new QName("http://order.bpel.tests.sca4j.org", "OrderProcess"), bpelProcessDefinition.getProcessName());

        List<ImportDefinition> imports = bpelProcessDefinition.getImports();
        assertEquals(3, imports.size());
        assertEquals("http://schemas.xmlsoap.org/wsdl/", imports.get(0).getImportType());
        assertEquals("billing.wsdl", imports.get(0).getLocation());
        assertEquals("http://billing.bpel.tests.sca4j.org", imports.get(0).getNamespace());
        assertEquals("http://schemas.xmlsoap.org/wsdl/", imports.get(1).getImportType());
        assertEquals("shipping.wsdl", imports.get(1).getLocation());
        assertEquals("http://shipping.bpel.tests.sca4j.org", imports.get(1).getNamespace());
        assertEquals("http://schemas.xmlsoap.org/wsdl/", imports.get(2).getImportType());
        assertEquals("order.wsdl", imports.get(2).getLocation());
        assertEquals("http://order.bpel.tests.sca4j.org", imports.get(2).getNamespace());

        List<PartnerLinkDefinition> partnerLinks = bpelProcessDefinition.getPartnerLinks();
        assertEquals(3, partnerLinks.size());
        assertEquals("order", partnerLinks.get(0).getName());
        assertEquals(new QName("http://order.bpel.tests.sca4j.org", "OrderPartnerLink"), partnerLinks.get(0).getType());
        assertEquals("orderService", partnerLinks.get(0).getMyRole());
        assertEquals("billing", partnerLinks.get(1).getName());
        assertEquals(new QName("http://billing.bpel.tests.sca4j.org", "BillingPartnerLink"), partnerLinks.get(1).getType());
        assertEquals("billingService", partnerLinks.get(1).getPartnerRole());
        assertEquals("shipping", partnerLinks.get(2).getName());
        assertEquals(new QName("http://shipping.bpel.tests.sca4j.org", "ShippingPartnerLink"), partnerLinks.get(2).getType());
        assertEquals("shippingService", partnerLinks.get(2).getPartnerRole());

        List<VariableDefinition> variables = bpelProcessDefinition.getVariables();
        assertEquals(6, variables.size());
        assertEquals("orderRequest", variables.get(0).getName());
        assertEquals(new QName("http://order.bpel.tests.sca4j.org", "OrderRequest"), variables.get(0).getType());
        assertEquals("orderResponse", variables.get(1).getName());
        assertEquals(new QName("http://order.bpel.tests.sca4j.org", "OrderResponse"), variables.get(1).getType());
        assertEquals("billingRequest", variables.get(2).getName());
        assertEquals(new QName("http://billing.bpel.tests.sca4j.org", "BillRequest"), variables.get(2).getType());
        assertEquals("billingResponse", variables.get(3).getName());
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "int"), variables.get(3).getType());
        assertEquals("shippingRequest", variables.get(4).getName());
        assertEquals(new QName("http://shipping.bpel.tests.sca4j.org", "ShipRequest"), variables.get(4).getType());
        assertEquals("shippingResponse", variables.get(5).getName());
        assertEquals(new QName("http://www.w3.org/2001/XMLSchema", "dateTime"), variables.get(5).getType());

        List<SequenceDefinition> sequences = bpelProcessDefinition.getSequences();
        assertEquals(1, sequences.size());

        SequenceDefinition sequenceDefinition = sequences.get(0);
        List<AbstractActivity> activities = sequenceDefinition.getActivities();
        assertEquals(6, activities.size());

        ReceiveDefinition receiveDefinition = (ReceiveDefinition) activities.get(0);
        assertEquals("placeOrder", receiveDefinition.getOperation());
        assertEquals("order", receiveDefinition.getPartnerLink());
        assertEquals("orderRequest", receiveDefinition.getVariable());

        AssignDefinition assignDefinition = (AssignDefinition) activities.get(1);
        List<CopyDefinition> copies =  assignDefinition.getCopies();
        assertEquals(4, copies.size());

        assertEquals("$orderRequest.itemCode", copies.get(0).getFrom());
        assertEquals("$billingRequest.itemCode", copies.get(0).getTo());
        assertEquals("$orderRequest.billingAddress", copies.get(1).getFrom());
        assertEquals("$billingRequest.billingAddress", copies.get(1).getTo());
        assertEquals("$orderRequest.itemCode", copies.get(2).getFrom());
        assertEquals("$shippingRequest.itemCode", copies.get(2).getTo());
        assertEquals("$orderRequest.shippingAddress", copies.get(3).getFrom());
        assertEquals("$shippingRequest.shippingAddress", copies.get(3).getTo());

        InvokeDefinition invokeDefinition = (InvokeDefinition) activities.get(2);
        assertEquals("bill", invokeDefinition.getOperation());
        assertEquals("billing", invokeDefinition.getPartnerLink());
        assertEquals("billingRequest", invokeDefinition.getInput());
        assertEquals("billingResponse", invokeDefinition.getOutput());

        invokeDefinition = (InvokeDefinition) activities.get(3);
        assertEquals("ship", invokeDefinition.getOperation());
        assertEquals("shipping", invokeDefinition.getPartnerLink());
        assertEquals("shippingRequest", invokeDefinition.getInput());
        assertEquals("shippingResponse", invokeDefinition.getOutput());

        assignDefinition = (AssignDefinition) activities.get(4);
        copies =  assignDefinition.getCopies();
        assertEquals(2, copies.size());
        assertEquals("$billingResponse", copies.get(0).getFrom());
        assertEquals("$orderResponse.amount", copies.get(0).getTo());
        assertEquals("$shippingResponse", copies.get(1).getFrom());
        assertEquals("$orderResponse.deliveryDate", copies.get(1).getTo());

        ReplyDefinition replyDefinition = (ReplyDefinition) activities.get(5);
        assertEquals("placeOrder", replyDefinition.getOperation());
        assertEquals("order", replyDefinition.getPartnerLink());
        assertEquals("orderResponse", replyDefinition.getVariable());
        
    }
    
}
