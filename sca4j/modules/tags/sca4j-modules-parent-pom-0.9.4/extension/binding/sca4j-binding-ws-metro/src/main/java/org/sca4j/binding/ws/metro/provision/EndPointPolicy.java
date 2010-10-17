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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sca4j.scdl.definitions.PolicySet;

/**
 * Contains PolicySets for the EndPoint keyed on operation names
 *
 */
public class EndPointPolicy {
	
	private Map<String, List<PolicySet>> policyMap = new HashMap<String, List<PolicySet>>();

	public void addPolicySets(String operation, List<PolicySet> providedPolicySets) {
		policyMap.put(operation, providedPolicySets);
	}

	public List<PolicySet> getPolicySets(String operation) {
		return policyMap.get(operation);
	}

	/**
	 * Returns Policies applied at EndPoint level. currently it is assumed that 
	 * 
	 */
	public List<PolicySet> getEndPointLevelPolicies() {
		return policyMap.values().iterator().hasNext() ? policyMap.values().iterator().next() : null;
	}
	
	
	
	
}
