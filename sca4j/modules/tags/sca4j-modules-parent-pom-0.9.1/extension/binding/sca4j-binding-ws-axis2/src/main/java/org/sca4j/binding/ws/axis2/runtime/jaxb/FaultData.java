package org.sca4j.binding.ws.axis2.runtime.jaxb;

import java.lang.reflect.Method;

import javax.xml.bind.JAXBContext;
import javax.xml.ws.WebFault;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;

public class FaultData {

    private final Object webFault;
    private final OMElement faultDetail;

    public FaultData(Object webFault, JAXBContext jaxbContext) {
        this.webFault = webFault;
        faultDetail = buildFaultElement(webFault, jaxbContext);
    }

    public AxisFault asAxisFault() {
        AxisFault fault = AxisFault.makeFault((Throwable) webFault);

        /* TODO: Add a control param to the binding so that we can replace the
         * fault detail with the stack trace without code changes
         * e.g. debugFaults=true/false
         * When true, the 'detail' element of SOAP faults will contain the full stack trace
         * of the underlying exception. When false, the 'detail' element will contain
         * the user specified fault detail (the return value of the getFaultInfo method on
         * the 'WebFault' annotated exception) (if not null) OR maybe the simple name of the
         * thrown exception.
         */
        //If faultDetail is null, the full stack trace will display.
        if(faultDetail != null) {
            fault.setDetail(faultDetail);
        }

        return fault;
    }

    private OMElement buildFaultElement(Object webFault, JAXBContext jaxbContext) {
        OMElement result = null;
        Object faultDetail = getFault(webFault);
        if(faultDetail != null) {
            result = new Jaxb2OMElement(jaxbContext).transform(faultDetail, null);
        }

        return result;
    }

    private Object getFault(Object webFault) {

        WebFault annotation = webFault.getClass().getAnnotation(WebFault.class);
        if (annotation == null) {
            // this is an undeclared exception
            if (webFault instanceof RuntimeException) {
                throw (RuntimeException) webFault;
            } else if (webFault instanceof Exception) {
                throw new AssertionError(webFault);
            } else if (webFault instanceof Error) {
                throw (Error) webFault;
            }
        }

        try {
            Method getFaultInfo = webFault.getClass().getMethod("getFaultInfo");
            return getFaultInfo.invoke(webFault);
        } catch (Exception e) {
            throw new AssertionError(e);
        }

    }


}
