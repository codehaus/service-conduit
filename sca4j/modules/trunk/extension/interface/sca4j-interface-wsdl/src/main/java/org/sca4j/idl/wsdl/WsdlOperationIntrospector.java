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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.namespace.QName;

import org.oasisopen.sca.annotation.EagerInit;
import org.sca4j.introspection.contract.OperationIntrospector;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ValidationContext;

@EagerInit
public class WsdlOperationIntrospector implements OperationIntrospector {

    @Override
    public <T> void introspect(Operation operation, Method method, ValidationContext context) {
        
        WebMethod webMethod = method.getAnnotation(WebMethod.class);
        if (webMethod != null) {
            operation.setWsdlName(webMethod.operationName());
        }
        
        List<DataType> inputTypes = operation.getInputTypes();
        for (int i = 0; i < inputTypes.size(); i++) {
            if (method.getParameterAnnotations()[i] == null) {
                continue;
            }
            for (Annotation annotation : method.getParameterAnnotations()[i]) {
                if (annotation.annotationType() == WebParam.class) {
                    WebParam webParam = WebParam.class.cast(annotation);
                    QName xsdType = new QName(webParam.targetNamespace(), webParam.name());
                    inputTypes.get(i).setXsdType(xsdType);
                }
            }
        }
        
        WebResult webResult = method.getAnnotation(WebResult.class);
        if (webResult != null) {
            QName xsdType = new QName(webResult.targetNamespace(), webResult.name());
            operation.getOutputType().setXsdType(xsdType);
        }

    }

}
