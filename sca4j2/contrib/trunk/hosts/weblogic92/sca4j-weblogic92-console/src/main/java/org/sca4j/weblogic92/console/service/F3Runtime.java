/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained 
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without 
 * warranties or conditions of any kind.  See the License for the specific language 
 * governing permissions and limitations of use of the software. This software is 
 * distributed in conjunction with other software licensed under different terms. 
 * See the separate licenses for those programs included in the distribution for the 
 * permitted and restricted uses of such software.
 *
 */

package org.sca4j.weblogic92.console.service;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents an F3 runtime.
 * 
 * @author meerajk
 *
 */
@XmlRootElement
@SuppressWarnings("unused")
public class F3Runtime {
	
	@XmlAttribute
	private String name;
	
	/**
	 * @param name Name (management sub-domain) of the F3 runtime.
	 */
	public F3Runtime(String name) {
		this.name = name;
	}

	/**
	 * Equality based on name.
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof F3Runtime && ((F3Runtime) obj).name.equals(name);
	}

	/**
	 * Hashcode based on name.
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * Default constructor.
	 */
	public F3Runtime() {
	}

}
