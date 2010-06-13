/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 */
package org.sca4j.binding.ws.metro.provision;

import javax.xml.namespace.QName;

public enum EndPointIntent {
	SOAP_V1_1("{http://docs.oasis-open.org/ns/opencsa/sca/200912}SOAP.v1_1"),
	SOAP_V1_2("{http://docs.oasis-open.org/ns/opencsa/sca/200912}SOAP.v1_1"),
	MessageOptimisation("{http://docs.oasis-open.org/ns/opencsa/sca/200912}messageOptimisation");
	
	private QName qName;
    EndPointIntent(String qName) {
    	this.qName = QName.valueOf(qName);
	}
    
	public QName getqName() {
    	return qName;
    }
	
	public EndPointIntent fromQName(QName qName) {

		for (EndPointIntent endPointIntent : EndPointIntent.values()) {
			if (endPointIntent.qName.equals(qName)) {
				return endPointIntent;
			}
		}
		throw new IllegalArgumentException("No values defined for " + qName);
	}
	
}
