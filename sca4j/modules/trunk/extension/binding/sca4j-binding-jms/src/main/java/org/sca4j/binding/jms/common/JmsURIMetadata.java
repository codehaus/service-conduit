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
package org.sca4j.binding.jms.common;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class JmsURIMetadata {
    public final static String CONNECTIONFACORYNAME = "connectionFactoryName";
    public final static String DESTINATIONTYPE = "destinationType";
    public final static String DELIVERYMODE = "deliveryMode";
    public final static String TIMETOLIVE = "timeToLive";
    public final static String PRIORITY = "priority";
    public final static String RESPONSEDESTINAT = "responseDestination";
    /**
     * string representative for destination
     */
    private String destination;
    /**
     * property map
     */
    private Map<String, String> properties;

    public String getDestination() {
        return destination;
    }

    private JmsURIMetadata(String destination) {
        this.destination = destination;
        properties = new HashMap<String, String>();
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Get a JmsURIMetadata from an input string.
     * 
     * @param uri string for /binding.jms/@uri
     * @return a JmsURIMetadata
     * @throws URISyntaxException Thrown when <code>uri</code> is not a valid
     *             format required by /binding.jms/@uri.
     */
    public static JmsURIMetadata parseURI(String uri) throws URISyntaxException {
        // TODO have a better validation
        boolean matches = Pattern.matches("jms:(.*?)[\\?(.*?)=(.*?)((&(.*?)=(.*?))*)]?", uri);
        if (!matches) {
            throw new URISyntaxException(uri, "Not a valid URI format for binding.jms");
        }
        return doParse(uri);
    }

    private static JmsURIMetadata doParse(String uri) {
        StringTokenizer token = new StringTokenizer(uri, ":?=&");
        String current;
        String propertyName = null;
        int pos = 0;
        JmsURIMetadata result = null;
        while (token.hasMoreTokens()) {
            current = token.nextToken();
            if (1 == pos) {
                result = new JmsURIMetadata(current);
            } else if (pos % 2 == 0) {
                propertyName = current;
            } else if (0 != pos) {// ignore beginning 'jms'
                assert propertyName != null;
                result.properties.put(propertyName.trim(), current.trim());
            }
            pos++;
        }
        return result;
    }

}
