package org.sca4j.binding.oracle.queue.spi.envelope;

/**
 * Properties Used for Envelopes
 */
public enum EnvelopeProperties {

    SCA_OPNAME("scaOperationName");
    
    
    final String name;
    
    /**
     * Construct By the alias Property Name
     * @param propertyName
     */
    EnvelopeProperties(String propertyName){
        name = propertyName;
    }
    
    /**
     * Return the Property Name
     * @return name
     */
    public String getValue(){
      return name;
    }
}
