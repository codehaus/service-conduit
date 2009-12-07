package org.sca4j.binding.oracle.queue.spi.envelope;

import java.util.HashMap;
import java.util.Map;

/**
 * The Default Implementation of the Envelope
 */
public class DefaultEnvelope implements Envelope {
  
	private static final long serialVersionUID = 8962720013695144155L;
	
	final private Object payload;
    final private Map<String, String> headers;
    
    /**
     * Initialise by the given attributes
     */
    public DefaultEnvelope(Object payload){
       this.payload = payload;
       headers = new HashMap<String, String>();
    }
    
    /**
     * Sets the Header Data    
     */
    public void setHeader(EnvelopeProperties envProp, String value){
      headers.put(envProp.getValue(), value);   
    }

    /**
     * Gets the Header Value
     */
    public String getHeaderValue(EnvelopeProperties envProp){
        return headers.get(envProp.getValue());
    }

    /**
     * Return the Payload
     */
    public Object getPayload() {       
        return payload;
    }

}
