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
package org.sca4j.binding.jms.common;

import java.net.URISyntaxException;
import java.util.Collections;
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
     * @throws URISyntaxException Thrown when <code>uri</code> is not a valid format required by /binding.jms/@uri.
     */
    public static JmsURIMetadata parseURI(String uri) throws URISyntaxException {
        //TODO have a better validation
        boolean matches = Pattern.matches(
                "jms:(.*?)[\\?(.*?)=(.*?)((&(.*?)=(.*?))*)]?", uri);
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
