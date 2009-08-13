package org.sca4j.junit.scdl;

import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;
import org.sca4j.scdl.BindingDefinition;

public class JUnitBindingDefinition extends BindingDefinition {
	
	private static final QName BINDING_QNAME = new QName(Namespaces.SCA4J_NS, "binding.junit");

	/**
	 * Default Constructor
	 */
    public JUnitBindingDefinition() {
        super(null, BINDING_QNAME, null);
    }

}
