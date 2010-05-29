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
package org.sca4j.bpel.introspection;

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
    static final String BPEL_NS = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
    static final String BPEL_PLINK_NS = "http://docs.oasis-open.org/wsbpel/2.0/plnktype";
    
    public static final QName PROCESS_ELEMENT = new QName(BPEL_NS, "process");
    public static final QName PARTNERLINK_ELEMENT = new QName(BPEL_NS, "partnerLink");
    public static final QName RECEIVE_ELEMENT = new QName(BPEL_NS, "receive");
    public static final QName INVOKE_ELEMENT = new QName(BPEL_NS, "invoke");
    public static final QName IMPORT_ELEMENT = new QName(BPEL_NS, "import");
    public static final QName VARIABLE_ELEMENT = new QName(BPEL_NS, "variable");
	public static final Object SEQUENCE_ELEMENT = new QName(BPEL_NS, "sequence");
	public static final Object ASSIGN_ELEMENT = new QName(BPEL_NS, "assign");
	public static final Object COPY_ELEMENT = new QName(BPEL_NS, "copy");
	public static final Object FROM_ELEMENT = new QName(BPEL_NS, "from");
	public static final Object TO_ELEMENT = new QName(BPEL_NS, "to");
	public static final Object REPLY_ELEMENT = new QName(BPEL_NS, "reply");

}
