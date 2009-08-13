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

package org.sca4j.introspection.impl.annotation;

import java.net.URI;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.DefaultIntrospectionContext;

import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.EagerInit;

@SuppressWarnings("unchecked")
public class EagerInitProcessorTestCase extends TestCase {

    public void testEagerInitWarning() throws Exception {
        TestClass componentToProcess = new TestClass();
        EagerInit annotation = componentToProcess.getClass().getAnnotation(EagerInit.class);
        EagerInitProcessor<Implementation<? extends InjectingComponentType>> processor =
                new EagerInitProcessor<Implementation<? extends InjectingComponentType>>();
        IntrospectionContext context = new DefaultIntrospectionContext((URI) null, null, null);
        processor.visitType(annotation, TestClass.class, new TestImplementation(), context);
        assertEquals(1, context.getWarnings().size());
        assertTrue(context.getWarnings().get(0) instanceof EagerInitNotSupported);
    }


    @Scope("CONVERSATION")
    @EagerInit
    public static class TestClass {
    }

    public static class TestImplementation extends Implementation {
        private static final long serialVersionUID = 2759280710238779821L;

        public QName getType() {
            return null;
        }
    }

}
