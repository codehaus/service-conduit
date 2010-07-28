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

import static java.lang.String.format;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import javax.inject.Inject;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.impl.annotation.InvalidAccessor;
import org.sca4j.introspection.java.AbstractAnnotationProcessor;
import org.sca4j.scdl.ConstructorInjectionSite;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.scdl.MethodInjectionSite;
import org.sca4j.scdl.Multiplicity;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceContract;

/**
 * Processes the JSR-330 <code>@Inject</code> annotation used by the JEE 6 CDI profile to identify an injection site. The
 * runtime handles the injection site as a {@link ReferenceDefinition}.   
 *
 * @param <I> The implementation type being introspected e.g. {@link org.sca4j.java.scdl.JavaImplementation}.
 */
public class InjectProcessor<I extends Implementation<? extends InjectingComponentType>> extends AbstractAnnotationProcessor<Inject, I> {
	
    private final ContractProcessor contractProcessor;
    private final IntrospectionHelper helper;
    
    /**********************************************************
     
     TODO: 
    	Add qualifier support.
      	Only edited ./modules/runtime/maven/sca4j-itest-plugin/src/main/resources/META-INF/sca4j/embeddedMaven.composite
      	
 	**********************************************************/      

    
    /**
     * Constructor.
     * @param contractProcessor build a contract definition based on the exposed interface of the component implementation.
     * @param helper utility introspection methods.
     */
    public InjectProcessor(@Reference ContractProcessor contractProcessor, @Reference IntrospectionHelper helper) {
        super(Inject.class);
        this.contractProcessor = contractProcessor;
        this.helper = helper;
    }
    
    /**
     * Process the annotated field encountered when walking a class.
     * @param annotation the annotation on the field.
     * @param field the annotated field.
     * @param implementation the component implementation being processed. 
     * @param context the broader context within which the introspection is happening.
     */
    public void visitField(Inject annotation, Field field, I implementation, IntrospectionContext context) {
        Member member = new Member(field);
		InvalidAccessor contextError = validateModifier(annotation, member);
        if (contextError != null) {        
        	context.addError(contextError);
        } else {
          FieldInjectionSite site = new FieldInjectionSite(field);          
          ReferenceDefinition definition = createDefinition(member, context);
          implementation.getComponentType().add(definition, site);        	
        }
    }

    /**
     * Process the annotated method encountered when walking a class.
     * @param annotation the annotation on the method.
     * @param method the annotated method.
     * @param implementation the component implementation being processed. 
     * @param context the broader context within which the introspection is happening.
     */    
    public void visitMethod(Inject annotation, Method method, I implementation, IntrospectionContext context) {
        Member member = new Member(method);
		InvalidAccessor contextError = validateModifier(annotation, member);
        if (contextError != null) {        
        	context.addError(contextError);
        } else {
        	final int parameterIndexZero = 0; //It's a setter method so always inject at index 0.
        	MethodInjectionSite site = new MethodInjectionSite(method, parameterIndexZero);
        	ReferenceDefinition definition = createDefinition(member, context);
        	implementation.getComponentType().add(definition, site);
        }
    }

    /**
     * Process the annotated constructor encountered when walking a class.
     * @param annotation the annotation on the constructor parameter.
     * @param constructor the annotated constructor.
     * @param implementation the component implementation being processed. 
     * @param context the broader context within which the introspection is happening.
     */    
    public void visitConstructor(Inject annotation, Constructor<?> constructor, I implementation, IntrospectionContext context) {  
    	//The constructor as a whole (not individual parameters) is the only allowed @Inject target. As per the CDI spec., this is
    	//interpreted as every parameter being an injection target. JSR-299 spec section 7.1: 
    	//"A bean constructor may have any number of parameters. All parameters of a bean constructor are injection points."    	    	
    	int parameterCount = constructor.getParameterTypes().length;
		for(int index = 0; index < parameterCount; index ++) {
        	Member member = new Member(constructor, index);    	
            ConstructorInjectionSite site = new ConstructorInjectionSite(constructor, index);
            ReferenceDefinition definition = createDefinition(member, context);
            implementation.getComponentType().add(definition, site);    		
    	}
    }

    /**
     * Create a {@link ReferenceDefinition} instance. This is added to the component type definition.
     * @param member the annotated component member.  
     * @param context the broader context within which the introspection is happening. 
     * @return a populated reference definition.
     */
    private ReferenceDefinition createDefinition(Member member, IntrospectionContext context) {        
        TypeMapping typeMapping = context.getTypeMapping();
        Type baseType = helper.getBaseType(member.genericType, typeMapping);
        ServiceContract contract = contractProcessor.introspect(typeMapping, baseType, context);
        Multiplicity multiplicity = multiplicity(member.genericType, typeMapping);
        return new ReferenceDefinition(member.name, contract, multiplicity);
    }

    /**
     * Returns the multiplicity of a type based on whether it describes a single value or a collection.
     * When present, @Inject designates a required injection so there is no ZERO_N multiplicity. 
     *
     * @param type the generic type of the injection target.
     * @param typeMapping the current introspection type mapping.
     * @return the multiplicity of the type.
     */
    private Multiplicity multiplicity(Type type, TypeMapping typeMapping) {
        return helper.isManyValued(typeMapping, type) ? Multiplicity.ONE_N : Multiplicity.ONE_ONE;        
    }
    
    /**
     * Validates the access modifier associated with the subject member. By limiting the valid modifiers to
     * public or protected, this implementation does not conform to the java.inject.Inject specification:
     * 
     * "An injectable member may have any access modifier (private, package-private, protected, public)." 
     * 
     * However, to comply with the introspection rules of other SCA components, the restriction of public or 
     * protected has been respected.
     * 
     * @param annotation the annotation on the member.
     * @param member the annotated member.
     * @return on error, a populated error structure, otherwise null. 
     */
    private InvalidAccessor validateModifier(Inject annotation, Member member) {
    	InvalidAccessor error = null;
        int fieldModifiers = member.modifiers;
		if (!Modifier.isProtected(fieldModifiers) && !Modifier.isPublic(fieldModifiers)) {
            Class<?> clazz = member.declaringClass;
            String modifier = Modifier.isPrivate(fieldModifiers) ? "private" : "package access";
            String msg = format("Invalid injection site. The @Inject annotated %s '%s' on '%s' is %s but should be public or protected.",
            		member.type, member.name, clazz.getName(), modifier);
            error = new InvalidAccessor(msg, clazz);
        }        
        return error;
    }
    
    /**
     * Simple (internal use only) adapter class for abstracting a reflection type.
     */
    private class Member {
    	
    	final int modifiers;
		final Class<?> declaringClass;
		final String name;
		final String type;
		final Type genericType;
	
    	public Member(Field field) {
    		modifiers = field.getModifiers();
    		declaringClass = field.getDeclaringClass();
    		name = field.getName();
    		type = "field";
    		genericType = field.getGenericType();
		}

    	public Member(Method method) {
    		modifiers = method.getModifiers();
    		declaringClass = method.getDeclaringClass();
    		type = "method";
            name = helper.getSiteName(method, null);
            genericType = helper.getGenericType(method);
		}
    	
    	public Member(Constructor<?> constructor, int index) {
    		modifiers = constructor.getModifiers();
    		declaringClass = constructor.getDeclaringClass();
    		type = "constructor";
    		name = helper.getSiteName(constructor, index, null);
            genericType = helper.getGenericType(constructor, index);
		}    	
    	
    }    
}
