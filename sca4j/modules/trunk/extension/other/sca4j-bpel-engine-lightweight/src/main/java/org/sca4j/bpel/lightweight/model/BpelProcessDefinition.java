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
package org.sca4j.bpel.lightweight.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.sca4j.spi.wire.InvocationChain;

/**
 * Created by IntelliJ IDEA. User: meerajk Date: May 29, 2010 Time: 10:33:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class BpelProcessDefinition {

    private QName processName;
    private List<VariableDefinition> variables = new ArrayList<VariableDefinition>();
    private List<ImportDefinition> imports = new ArrayList<ImportDefinition>();
    private List<SequenceDefinition> sequences = new ArrayList<SequenceDefinition>();
    private List<PartnerLinkDefinition> partnerLinks = new ArrayList<PartnerLinkDefinition>();
    private Map<String, InvocationChain> invocationChains = new HashMap<String, InvocationChain>();

    public BpelProcessDefinition(QName processName) {
        this.processName = processName;
    }

    public QName getProcessName() {
        return processName;
    }

    public List<ImportDefinition> getImports() {
        return imports;
    }

    public List<VariableDefinition> getVariables() {
        return variables;
    }

    public List<PartnerLinkDefinition> getPartnerLinks() {
        return partnerLinks;
    }

    public List<SequenceDefinition> getSequences() {
        return sequences;
    }

    public SequenceDefinition getLastSequence() {
        return getSequences().get(getSequences().size() - 1);
    }
    
    public Map<String, InvocationChain> getInvocationChains() {
        return invocationChains;
    }

    public void addInvoker(String partnerLink, String operation, InvocationChain invocationChain) {
        invocationChains.put(partnerLink + "/" + operation, invocationChain);
    }
    
}
