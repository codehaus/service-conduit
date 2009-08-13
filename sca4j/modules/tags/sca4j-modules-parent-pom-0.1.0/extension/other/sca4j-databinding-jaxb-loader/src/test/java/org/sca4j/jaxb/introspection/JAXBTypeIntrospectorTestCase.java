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
