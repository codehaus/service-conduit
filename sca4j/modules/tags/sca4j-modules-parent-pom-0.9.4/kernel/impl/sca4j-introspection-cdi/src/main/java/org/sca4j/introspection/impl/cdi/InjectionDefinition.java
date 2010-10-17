package org.sca4j.introspection.impl.cdi;

import org.sca4j.introspection.impl.cdi.annotation.QualifiedInjectionSite;
import org.sca4j.scdl.Multiplicity;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceContract;

/**
 * Interim commit: Possibly temporary class.
 */
public class InjectionDefinition extends ReferenceDefinition {

	private final QualifiedInjectionSite injectionQualifier;

	public InjectionDefinition(String name, ServiceContract serviceContract, 
							   Multiplicity multiplicity, QualifiedInjectionSite injectionQualifier) {
		super(name, serviceContract, multiplicity);
		this.injectionQualifier = injectionQualifier;
	}	

}
