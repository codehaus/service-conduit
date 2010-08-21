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
package org.sca4j.bpel.model;

import javax.xml.namespace.QName;

import org.oasisopen.sca.Constants;
import org.sca4j.scdl.Implementation;

/**
 * Represents a BPEL implementation.
 *
 */
public class BpelImplementation extends Implementation<BpelComponentType> {
    
    public static final QName IMPLEMENTATION_BPEL = new QName(Constants.SCA_NS, "implementation.bpel");

    @Override
    public QName getType() {
        return IMPLEMENTATION_BPEL;
    }

}
