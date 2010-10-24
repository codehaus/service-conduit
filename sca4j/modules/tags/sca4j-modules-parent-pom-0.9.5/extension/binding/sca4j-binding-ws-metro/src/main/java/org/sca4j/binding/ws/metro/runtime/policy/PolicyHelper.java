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
package org.sca4j.binding.ws.metro.runtime.policy;

import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

import org.sca4j.binding.ws.metro.provision.EndPointIntent;
import org.sca4j.binding.ws.metro.provision.EndPointPolicy;
import org.sca4j.scdl.definitions.PolicySet;

import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.binding.WebServiceFeatureList;

public class PolicyHelper {
	
	public BindingID getBindingId(EndPointPolicy policyDefinition) {
		for (PolicySet policySet : policyDefinition.getEndPointLevelPolicies()) {
			if (policySet.doesProvide(EndPointIntent.SOAP_V1_2.qName())) {
				return BindingID.SOAP12_HTTP;
			}
		}
		return BindingID.SOAP11_HTTP;// default
	}
	
	public WebServiceFeature[] getWSFeatures(EndPointPolicy policyDefinition) {
		WebServiceFeatureList wsFeatureList = new WebServiceFeatureList();
		for (PolicySet policySet : policyDefinition.getEndPointLevelPolicies()) {
			if(policySet.doesProvide(EndPointIntent.MessageOptimisation.qName())) {
				wsFeatureList.add(new MTOMFeature(true));
			}
		}
		return wsFeatureList.toArray();
	}

}
