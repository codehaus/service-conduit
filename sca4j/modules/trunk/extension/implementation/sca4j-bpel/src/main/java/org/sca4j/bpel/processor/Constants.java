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
package org.sca4j.bpel.processor;

import javax.xml.namespace.QName;

/**
 * Enumeration of all the qualifid names.
 * 
 * @author meerajk
 * 
 */
public class Constants {

    static final String SCA_BPEL_NS = "http://docs.oasis-open.org/ns/opencsa/sca-bpel/200801";
    static final String WSDL_NS = "http://schemas.xmlsoap.org/wsdl/";
    static final String BPEL_NS_20 = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
    static final String BPEL_PLINK_NS_20 = "http://docs.oasis-open.org/wsbpel/2.0/plnktype";
    
    static final QName PROCESS_ELEMENT_20 = new QName(BPEL_NS_20, "process");
    static final QName PARTNERLINK_ELEMENT_20 = new QName(BPEL_NS_20, "partnerLink");
    static final QName ONEVENT_ELEMENT_20 = new QName(BPEL_NS_20, "onEvent");
    static final QName RECEIVE_ELEMENT_20 = new QName(BPEL_NS_20, "receive");
    static final QName ONMESSAGE_ELEMENT_20 = new QName(BPEL_NS_20, "onMessage");
    static final QName INVOKE_ELEMENT_20 = new QName(BPEL_NS_20, "invoke");
    static final QName IMPORT_ELEMENT_20 = new QName(BPEL_NS_20, "import");
    static final QName VARIABLE_ELEMENT_20 = new QName(BPEL_NS_20, "variable");
    static final QName LINKTYPE_ELEMENT_20 = new QName(BPEL_PLINK_NS_20, "partnerLinkType");

}
