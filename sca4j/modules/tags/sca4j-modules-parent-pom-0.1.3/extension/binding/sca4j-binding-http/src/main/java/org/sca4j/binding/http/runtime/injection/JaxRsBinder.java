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
package org.sca4j.binding.http.runtime.injection;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
            queryParameters.put(pair[0], pair.length < 2 ? "" : pair[1]);
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
