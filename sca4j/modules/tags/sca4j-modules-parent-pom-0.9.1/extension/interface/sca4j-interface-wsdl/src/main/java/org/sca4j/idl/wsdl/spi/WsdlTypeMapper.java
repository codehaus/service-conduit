package org.sca4j.idl.wsdl.spi;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

public class WsdlTypeMapper {
    
    private Map<QName, Class<?>> mappings = new HashMap<QName, Class<?>>();
    
    public void register(QName xmlType, Class<?> javaType) {
        mappings.put(xmlType, javaType);
    }

    public Class<?> get(QName xmlType) {
        return mappings.get(xmlType);
    }
}
