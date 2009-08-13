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

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.sca4j.api.annotation.scope.Scopes;
import org.sca4j.api.annotation.scope.Stateless;
import org.sca4j.scdl.AbstractComponentType;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;

@SuppressWarnings("unchecked")
public class StatelessProcessorTestCase extends TestCase {
    
    public void testScopeIdentification() throws Exception {
        
        StatelessAnnotated componentToProcess = new StatelessAnnotated();
        Stateless annotation = componentToProcess.getClass().getAnnotation(Stateless.class);        
        StatelessProcessor<Implementation<? extends InjectingComponentType>> processor = 
                                new StatelessProcessor<Implementation<? extends InjectingComponentType>>();        
        processor.visitType(annotation, componentToProcess.getClass(), componentToProcess, null);
        
        assertEquals("Unexpected scope", Scopes.STATELESS, componentToProcess.getScope());
    }
    
    @SuppressWarnings("serial")
    @Stateless
    public static class StatelessAnnotated extends Implementation {        
        
        private String scope;
        
        public String getScope() {
            return scope;
        }

        @Override
        public AbstractComponentType getComponentType() {
            return new InjectingComponentType() {
                @Override
                public void setScope(String introspectedScope) {
                    scope = introspectedScope;
                }
            };
        };
        
        @Override
        public QName getType() {
            return null;
        }
    }
    
}
