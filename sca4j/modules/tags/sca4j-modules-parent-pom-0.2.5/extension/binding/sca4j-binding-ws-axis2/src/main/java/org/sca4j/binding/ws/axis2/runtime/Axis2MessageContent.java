package org.sca4j.binding.ws.axis2.runtime;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.sca4j.binding.ws.axis2.runtime.jaxb.MessageData;

public class Axis2MessageContent {

    private OMElement body;
    private final List<OMElement> headers = new ArrayList<OMElement>();

    //The body will always contain the last parameter mapped to the body.
    //If there are >1 parameters mapped to the body, the others will be ignored.
    public Axis2MessageContent(Object[] payload) {
        if(payload != null) {
            for (Object object : payload) {
                if(object instanceof MessageData) {
                    MessageData messageData = (MessageData) object;
                    if(messageData.isHeader()) {
                        headers.add(messageData.getOmElement());
                    } else {
                        body = messageData.getOmElement();
                    }
                } else {
                    body = (OMElement)object;
                }
            }
        }

    }

    public OMElement getBody() {
        return body;
    }

    public List<OMElement> getHeaders() {
        return headers;
    }

}
