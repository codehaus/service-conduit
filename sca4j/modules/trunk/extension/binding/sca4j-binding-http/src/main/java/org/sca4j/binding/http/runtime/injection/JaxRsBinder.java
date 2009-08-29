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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.binding.http.runtime.injection;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;

import org.sca4j.binding.http.runtime.introspection.OperationMetadata;
import org.sca4j.binding.http.runtime.introspection.ParameterMetadata;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;

/**
 * Implementation of bare binder.
 *
 */
public class JaxRsBinder implements DataBinder {

    public void marshal(HttpServletResponse response, Message message) throws IOException {
        byte[] data = message.getBody().toString().getBytes();
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(data, 0, data.length);
        outputStream.flush();
    }

    public Message unmarshal(HttpServletRequest request, OperationMetadata operation) throws IOException {
        
        List<ParameterMetadata> parameters = operation.getParameters();
        Object[] body = new Object[parameters.size()];
        String queryString = request.getQueryString();
        
        Map<String, String> queryParameters = new HashMap<String, String>();
        StringTokenizer tok = new StringTokenizer(queryString == null ? "" : queryString, "&");
        while (tok.hasMoreElements()) {
            String[] pair = tok.nextToken().split("=");
            queryParameters.put(pair[0], pair.length < 2 ? "" : URLDecoder.decode(pair[1], "UTF-8"));
        }
        
        for (int i = 0; i < body.length;i++) {
            
            ParameterMetadata parameter = parameters.get(i);
            Class<?> type = parameter.getType();
            Annotation annotation = parameter.getAnnotation();
            
            if (annotation == null) {
                body[i] = createEntity(request, queryParameters, type);
            } else if (annotation.annotationType().equals(QueryParam.class)) {
                body[i] = getFromQueryString(QueryParam.class.cast(annotation), type, queryParameters);
            } else if (annotation.annotationType().equals(HeaderParam.class)) {
                body[i] = getFromHeader(HeaderParam.class.cast(annotation), type, request);
            }
            
        }
        
        return new MessageImpl(body, false, new WorkContext());
    }

    private Object createEntity(HttpServletRequest request, Map<String, String> queryParameters, Class<?> type) {
        
        try {
            Object entity = type.newInstance();
            for (Field field : getFieldList(type)) {
                QueryParam queryParam = field.getAnnotation(QueryParam.class);
                if (queryParam != null) {
                    field.setAccessible(true);
                    Object value = getFromQueryString(queryParam, field.getType(), queryParameters);
                    if(value != null) {
                    	//Set the value for the named field if it had a corresponding parameter in the query string. Otherwise, let the default value apply
                    	field.set(entity, value);
                    }
                } else {
                    HeaderParam headerParam = field.getAnnotation(HeaderParam.class);
                    if (headerParam != null) {
                        field.setAccessible(true);
                        Object value = getFromHeader(headerParam, field.getType(), request);
                        if(value != null) {
                        	//Set the value for the named field if it had a corresponding parameter in the header. Otherwise, let the default value apply
                        	field.set(entity, value);
                        }
                    }
                }
            }
            return entity;
        } catch (InstantiationException e) {
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
        
    }
    
    private List<Field> getFieldList(Class<?> type) {
    	List<Field> fields = new ArrayList<Field>();
    	if(type != null & !type.equals(Object.class)) {
        	fields.addAll(Arrays.asList(type.getDeclaredFields()));
        	fields.addAll(getFieldList(type.getSuperclass()));
    	}
		return fields;
	}

	private Object getFromHeader(HeaderParam headerParam, Class<?> type, HttpServletRequest request) {
        String text = request.getHeader(headerParam.value());
        return getValue(text, type);
    }
    
    private Object getFromQueryString(QueryParam queryParam, Class<?> type, Map<String, String> queryParameters) {
        String text = queryParameters.get(queryParam.value());
        return getValue(text, type);
    }
    
    private Object getValue(String text, Class<?> type) {
    	if(text == null) {
    		return null;
    	}
        PropertyEditor propertyEditor = PropertyEditorManager.findEditor(type);
        propertyEditor.setAsText(text);
        return propertyEditor.getValue();
    }

}
