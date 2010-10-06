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
 */
package org.sca4j.transform.dom2java.generics.map;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import javax.xml.namespace.QName;

import org.sca4j.scdl.DataType;
import org.sca4j.spi.model.type.JavaParameterizedType;
import org.sca4j.transform.TransformationException;

public class String2MapOfQName2String extends String2Map<QName, String> {
    
    @SuppressWarnings("unused")
    private static Map<QName, String> FIELD;

    public String2MapOfQName2String() {
        super(QName.class, String.class);
    }

    @Override
    public DataType getTargetType() {
        try {
            return new JavaParameterizedType((ParameterizedType) String2MapOfQName2String.class.getDeclaredField("FIELD").getGenericType());
        } catch (NoSuchFieldException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    protected QName buildKey(String tagName, String namespaceUri) throws TransformationException {
        return new QName(namespaceUri, tagName);
    }
    
    @Override
    protected String buildValue(String textContent) throws TransformationException {
        return String.valueOf(textContent);
    }
    
}
