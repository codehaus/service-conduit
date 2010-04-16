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
package org.sca4j.idl.wsdl;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.sca4j.introspection.contract.OperationIntrospector;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ValidationContext;

@EagerInit
public class WsdlOperationIntrospector implements OperationIntrospector {

    private Map<Class<?>, QName> xsdTypes = new HashMap<Class<?>, QName>();
    
    @Init
    public void start() {
        xsdTypes.put(int.class, new QName(W3C_XML_SCHEMA_NS_URI, "int"));
        xsdTypes.put(String.class, new QName(W3C_XML_SCHEMA_NS_URI, "string"));
        // TODO Add more types
    }
    
    @Override
    public <T> void introspect(Operation operation, Method method, ValidationContext context) {
        
        WebMethod webMethod = method.getAnnotation(WebMethod.class);
        if (webMethod != null) {
            operation.setWsdlName(webMethod.operationName());
        }
        
        List<DataType> inputTypes = operation.getInputTypes();
        for (int i = 0; i < inputTypes.size(); i++) {
            Class<?> parameterJavaType = method.getParameterTypes()[i];
            QName parameterXsdType = getXsdType(parameterJavaType);
            inputTypes.get(i).setXsdType(parameterXsdType);
        }
        
        Class<?> returnJavaType = method.getReturnType();
        QName returnXsdType = getXsdType(returnJavaType);
        operation.getOutputType().setXsdType(returnXsdType);
        
    }

    /*
     * Gets the XSD type for a Java type.
     */
    private QName getXsdType(Class<?> javaType) {
        if (javaType == null) {
            return null;
        }
        QName xsdType = xsdTypes.get(javaType);
        if (xsdType == null) {
            XmlType xmlType = javaType.getAnnotation(XmlType.class);
            XmlSchema xmlSchema = javaType.getPackage().getAnnotation(XmlSchema.class);
            if (xmlType != null && xmlSchema != null) {
                xsdType = new QName(xmlSchema.namespace(), xmlType.name());
            }
        }
        return xsdType;
    }

}
