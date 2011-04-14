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
package org.sca4j.binding.http.runtime.invocation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.oasisopen.sca.ServiceUnavailableException;
import org.sca4j.binding.http.provision.security.AuthenticationPolicy;
import org.sca4j.binding.http.runtime.introspection.DataBinding;
import org.sca4j.binding.http.runtime.introspection.OperationMetadata;
import org.sca4j.binding.http.runtime.invocation.security.ConnectionProvider;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.wire.Interceptor;

/**
 * Target interceptor for HTTP binding.
 *
 */
public class HttpBindingInterceptor<T extends AuthenticationPolicy> implements Interceptor {

    private Interceptor next;
    private URL url;
    private long timeout;
    private OperationMetadata operationMetadata;
    private T authenticationPolicy;
    private ConnectionProvider<T> connectionProvider;
    private URI classLoaderId;

    public HttpBindingInterceptor(URL url,
                                  OperationMetadata operationMetadata,
                                  ConnectionProvider<T> connectionProvider,
                                  T authenticationPolicy,
                                  URI classLoaderId, long timeout) {
        this.url = url;
        this.operationMetadata = operationMetadata;
        this.authenticationPolicy = authenticationPolicy;
        this.connectionProvider = connectionProvider;
        this.classLoaderId = classLoaderId;
        this.timeout = timeout;
    }

    public Interceptor getNext() {
        return next;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

    /**
     * TODO Support form encoded as well as other entity bodies.
     */
    public Message invoke(Message message) {

        HttpMethod httpMethod = null;
        HttpClient httpClient = null;
        ByteArrayOutputStream response = null;

        try {

            httpClient = timeout == 0L ? connectionProvider.createClient(authenticationPolicy, url, classLoaderId) :
                                         connectionProvider.createClient(authenticationPolicy, url, classLoaderId, timeout);

            if (operationMetadata.getHttpMethod() == org.sca4j.binding.http.runtime.introspection.HttpMethod.GET) {
                httpMethod = new GetMethod(url.toExternalForm());
            } else {
                httpMethod = new PostMethod(url.toExternalForm());
            }

            Object[] payload = (Object[]) message.getBody();
            httpMethod.setQueryString(getQueryParameters(payload));
            for (Map.Entry<String, String> headerParameter : getHeaderParameters(payload).entrySet()) {
                httpMethod.addRequestHeader(headerParameter.getKey(), headerParameter.getValue());
            }

            httpClient.executeMethod(httpMethod);
            response = getResponse(httpMethod);

            httpMethod.releaseConnection();

            message.setBody(response.toString());
            return message;

        } catch (IllegalAccessException e) {
            throw new ServiceUnavailableException(e);
        } catch (HttpException e) {
            throw new ServiceUnavailableException(e);
        } catch (IOException e) {
            throw new ServiceUnavailableException(e);
        }

    }


	private ByteArrayOutputStream getResponse(HttpMethod httpMethod) throws IOException {

		ByteArrayOutputStream byteArrayResp = new ByteArrayOutputStream();
		IOUtils.copy(httpMethod.getResponseBodyAsStream(), byteArrayResp);
		return byteArrayResp;
	}

    private Map<String, String> getHeaderParameters(Object[] payload) throws IllegalAccessException {

        Map<String, String> headerParameters = new HashMap<String, String>();
        if (operationMetadata.getInBinding() == DataBinding.JAXRS) {
            for (int i = 0; i < payload.length;i++) {
                Annotation annotation = operationMetadata.getParameters().get(i).getAnnotation();
                if (annotation == null) {
                    for (Field field :payload[i].getClass().getDeclaredFields()) {
                        HeaderParam headerParam = field.getAnnotation(HeaderParam.class);
                        if (headerParam != null) {
                            field.setAccessible(true);
                            headerParameters.put(headerParam.value(), field.get(payload[i]).toString());
                        }
                    }
                } else if (annotation.annotationType() == HeaderParam.class) {
                    HeaderParam headerParam = HeaderParam.class.cast(annotation);
                    headerParameters.put(headerParam.value(), payload[i].toString());
                }
            }
        }

        return headerParameters;

    }

    private NameValuePair[] getQueryParameters(Object[] payload) throws IllegalAccessException {

        List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();

        if (operationMetadata.getInBinding() == DataBinding.JAXRS) {
            for (int i = 0; i < payload.length;i++) {
                Annotation annotation = operationMetadata.getParameters().get(i).getAnnotation();
                if (annotation == null) {
                	nameValuePairs.addAll(getFieldValues(payload[i].getClass(), payload[i]));
                } else if (annotation.annotationType() == QueryParam.class) {
                    QueryParam queryParam = QueryParam.class.cast(annotation);
                    nameValuePairs.add(new NameValuePair(queryParam.value(), payload[i].toString()));
                }
            }
        }

        return nameValuePairs.toArray(new NameValuePair[nameValuePairs.size()]);

    }

    private List<NameValuePair> getFieldValues(Class<?> clazz, Object payloadObject) throws IllegalAccessException {
    	List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
    	if(clazz != null && !clazz.equals(Object.class)) {
            for (Field field : clazz.getDeclaredFields()) {
                QueryParam queryParam = field.getAnnotation(QueryParam.class);
                if (queryParam != null) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(payloadObject);
                    if(fieldValue != null) {
                    	nameValuePairs.add(new NameValuePair(queryParam.value(), fieldValue.toString()));
                    }
                }
            }

            nameValuePairs.addAll(getFieldValues(clazz.getSuperclass(), payloadObject));
    	}

    	return nameValuePairs;
    }


}
