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


/**
 * Created by IntelliJ IDEA. User: meerajk Date: May 29, 2010 Time: 10:39:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReceiveDefinition extends AbstractActivity {

    private String operation;
    private String partnerLink;
    private String variable;

    public ReceiveDefinition(String operation, String partnerLink, String variable) {
        this.operation = operation;
        this.partnerLink = partnerLink;
        this.variable = variable;
    }

    public String getOperation() {
        return operation;
    }

    public String getPartnerLink() {
        return partnerLink;
    }

    public String getVariable() {
        return variable;
    }
    
    public Type getType() {
        return Type.RECEIVE;
    }

}
