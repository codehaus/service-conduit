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
 *
 *
 * ---- Original Codehaus Header ----
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
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
 *
 * ---- Original Apache Header ----
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
 */
package org.sca4j.jaxb.introspection;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

import junit.framework.TestCase;

import org.sca4j.jaxb.provision.JAXBConstants;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;

/**
 * @version $Revision$ $Date$
 */
public class JAXBTypeIntrospectorTestCase extends TestCase {
    private JAXBTypeIntrospector introspector;

    public void testJAXBIntrospection() throws Exception {
        Operation<Type> jaxbOperation = createOperation("jaxbMethod", Param.class);
        introspector.introspect(jaxbOperation, Contract.class.getMethod("jaxbMethod", Param.class), null);
        assertTrue(jaxbOperation.getIntents().contains(JAXBConstants.DATABINDING_INTENT));
    }

    public void testNoJAXBIntrospection() throws Exception {
        Operation<Type> nonJaxbOperation = createOperation("nonJaxbMethod", String.class);
        introspector.introspect(nonJaxbOperation, Contract.class.getMethod("nonJaxbMethod", String.class), null);
        assertFalse(nonJaxbOperation.getIntents().contains(JAXBConstants.DATABINDING_INTENT));
    }

    protected void setUp() throws Exception {
        super.setUp();
        introspector = new JAXBTypeIntrospector();
    }

    private Operation<Type> createOperation(String name, Class<?> paramType) {
        DataType<Type> type = new DataType<Type>(paramType, paramType);
        List<DataType<Type>> in = new ArrayList<DataType<Type>>();
        in.add(type);
        DataType<List<DataType<Type>>> inParams = new DataType<List<DataType<Type>>>(paramType, in);
        return new Operation<Type>(name, inParams, null, null);
    }

    private class Contract {
        public void jaxbMethod(Param param) {

        }

        public void nonJaxbMethod(String param) {

        }
    }

    @XmlRootElement
    private class Param {

    }

}
