package org.sca4j.binding.oracle.queue.spi.envelope;

import java.io.Serializable;

/**
 * Envelope used to pass between queues
 */
public interface Envelope extends Serializable {
    
    /**
     * Sets the Header Data
     * @param - the header name
     * @param - the header value    
     */
    void setHeader(EnvelopeProperties envProp, String value);
    
    /**
     * Gets the Header Data 
     */
    String getHeaderValue(EnvelopeProperties envProp);
    
    /**
     * Returns the actual payload.
     * @return Object - payload
     */
    Object getPayload();

}
