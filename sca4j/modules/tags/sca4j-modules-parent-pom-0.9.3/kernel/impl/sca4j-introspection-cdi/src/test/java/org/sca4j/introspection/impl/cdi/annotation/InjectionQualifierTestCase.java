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
package org.sca4j.introspection.impl.cdi.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import junit.framework.TestCase;

/**
 * JUnit tests for @Inject processing where a qualifier is present.
 */
public class InjectionQualifierTestCase extends TestCase {

	/**
	 * Tests unqualified injection.
	 * @throws Exception on error.
	 */
    public void testUnqualified() throws Exception {      	
    	Field field = QualifierTestPrivateClass.class.getDeclaredField("anything");
    	QualifiedInjectionSite injectionQualifier = new QualifiedInjectionSite(field);
    	
    	assertFalse(injectionQualifier.isQualified());
    	assertEquals(0, injectionQualifier.getQualifiers().size());
	}
    
    /**
     * Tests qualified field injection.
     * @throws Exception on error.
     */
    public void testField() throws Exception {      	
    	Field field = QualifierTestPrivateClass.class.getDeclaredField("coffee");
    	QualifiedInjectionSite injectionQualifier = new QualifiedInjectionSite(field);
    	
    	assertTrue(injectionQualifier.isQualified());
    	assertEquals(1, injectionQualifier.getQualifiers().size());
    	assertTrue(injectionQualifier.getQualifiers().get(0).annotationType().getName().endsWith("Coffee"));
	}
    
    /**
     * Tests qualified method injection based on the {@link Named} annotation.
     * @throws Exception on error.
     */
    public void testNamedQualifierField() throws Exception {      	
    	Field field = QualifierTestPrivateClass.class.getDeclaredField("tea");
    	QualifiedInjectionSite injectionQualifier = new QualifiedInjectionSite(field);
    	
    	assertTrue(injectionQualifier.isQualified());
    	assertEquals(1, injectionQualifier.getQualifiers().size());
    	assertTrue(injectionQualifier.getQualifiers().get(0).annotationType().getName().endsWith("Named"));
    	Named qualifier = injectionQualifier.getQualifier(Named.class, 0);
    	assertEquals("Tea", qualifier.value());
    }
    
    /**
     * Tests qualified method injection.
     * @throws Exception on error.
     */    
    public void testMethod() throws Exception {    
    	final int parameterIndexZero = 0; 
    	String[] expectedQualifierNames = {"Hot", "Water"};
    	
    	Method method = QualifierTestPrivateClass.class.getDeclaredMethod("setHotDrink", Drink.class);
    	QualifiedInjectionSite injectionQualifier = new QualifiedInjectionSite(method, parameterIndexZero);
    	
    	assertAnnotationState(expectedQualifierNames, injectionQualifier);
	}
    
    /**
     * Tests qualified constructor injection.
     * @throws Exception on error.
     */    
    public void testConstructor() throws Exception {    
    	Constructor<QualifierTestPrivateClass> constructor = QualifierTestPrivateClass.class.getDeclaredConstructor(Drink.class, Drink.class);
    	QualifiedInjectionSite firstParam = new QualifiedInjectionSite(constructor, 0);
    	QualifiedInjectionSite secondParam = new QualifiedInjectionSite(constructor, 1);
    	assertAnnotationState(new String[]{"Water"}, firstParam);
    	Water qualifier = firstParam.getQualifier(Water.class, 0);
    	assertTrue(qualifier.fizzy());
    	
    	assertAnnotationState(new String[]{"Hot", "Water"}, secondParam);
    	qualifier = secondParam.getQualifier(Water.class, 1);
    	assertFalse(qualifier.fizzy());    	
	}
    
    /**
     * Utility method.
     * @param expectedQualifierNames expected annotation short names.
     * @param injectionQualifier the populated qualifier.
     */
	private void assertAnnotationState(String[] expectedQualifierNames, QualifiedInjectionSite injectionQualifier) {
		assertTrue(injectionQualifier.isQualified());
    	assertEquals(expectedQualifierNames.length, injectionQualifier.getQualifiers().size());
    	int idx = 0;    	
    	for (Annotation annotation: injectionQualifier.getQualifiers()) {
    		assertTrue(annotation.annotationType().getName().endsWith(expectedQualifierNames[idx]));
    		idx ++;
		}
	}    
    
    /** Test support classes **/
    static class QualifierTestPrivateClass {
    	
    	protected @Inject @Any @Default Drink anything;
    	protected @Inject @Coffee Drink coffee;
    	protected @Inject @Named("Tea") Drink tea;
    	
    	@Inject
    	private QualifierTestPrivateClass(@Water(fizzy=true) Drink sparkling, @Hot @Water(fizzy=false) Drink still) {}
    	
    	@Inject
    	protected void setHotDrink(@Hot @Water(fizzy=false) Drink hotWater) {}
    	
    }
        
    @Qualifier
    @Retention(RUNTIME)
    @Target({METHOD, FIELD, PARAMETER, TYPE})    
    @interface Coffee {
    	
    }
    
    @Qualifier
    @Retention(RUNTIME)
    @Target({METHOD, FIELD, PARAMETER, TYPE})    
    @interface Water {
    	boolean fizzy() default true;
    }
    
    @Qualifier
    @Retention(RUNTIME)
    @Target({METHOD, FIELD, PARAMETER, TYPE})    
    @interface Hot {
    	boolean fizzy() default true;
    }       
    
    interface Drink {    	    	
    	String getName();
    }
    
    /* TODO: use when testing wiring.
     
    @Coffee
    class CupOfCoffee implements Drink {
		@Override
		public String getName() {
			return "Coffee";
		}
    }
    
    @Named("Tea")
    class CupOfTea implements Drink {
		@Override
		public String getName() {
			return "Tea";
		}    	
    }    
    
    @Water(fizzy=true)
    class ColdSparklingWater implements Drink {
		@Override
		public String getName() {
			return "SparklingWater";
		}    	
    }    
    
    @Water(fizzy=false)
    class ColdStillWater implements Drink {
		@Override
		public String getName() {
			return "StillWater";
		}    	
    }
    
    @Hot
    @Water(fizzy=false)
    class HotStillWater implements Drink {
		@Override
		public String getName() {
			return "StillWater";
		}    	
    }
    
    */         
}
