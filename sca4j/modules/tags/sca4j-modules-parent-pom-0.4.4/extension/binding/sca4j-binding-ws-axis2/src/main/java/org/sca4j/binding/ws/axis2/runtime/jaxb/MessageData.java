package org.sca4j.binding.ws.axis2.runtime.jaxb;

import org.apache.axiom.om.OMElement;

public class MessageData {

    private final OMElement omElement;
    private final boolean header;

    public MessageData(OMElement omElement, boolean header) {
        this.omElement = omElement;
        this.header = header;
    }

    public OMElement getOmElement() {
        return omElement;
    }

    public boolean isHeader() {
        return header;
    }

}
