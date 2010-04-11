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
package org.sca4j.tests.function.properties;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import junit.framework.TestCase;
import org.oasisopen.sca.annotation.Reference;

/**
 * @version $Rev: 3152 $ $Date: 2008-03-21 23:00:25 +0000 (Fri, 21 Mar 2008) $
 */
public class PropertyTypeTest extends TestCase {
    @Reference
    public PropertyTypes service;

    public void testBoolean() {
        assertEquals(true, service.getBooleanPrimitive());
    }

    public void testByte() {
        assertEquals((byte)12, service.getBytePrimitive());
    }

    public void testShort() {
        assertEquals((short)1234, service.getShortPrimitive());
    }

    public void testInteger() {
        assertEquals(12345678, service.getIntPrimitive());
    }

    public void testLong() {
        assertEquals(123451234512345l, service.getLongPrimitive());
    }

    public void testFloat() {
        assertEquals(1.2345f, service.getFloatPrimitive());
    }

    public void testDouble() {
        assertEquals(1.2345e10, service.getDoublePrimitive());
    }

    public void testString() {
        assertEquals("Hello World", service.getString());
    }

    public void testBooleanValue() {
        assertEquals(Boolean.TRUE, service.getBooleanValue());
    }

    public void testByteValue() {
        assertEquals(Byte.valueOf((byte)12), service.getByteValue());
    }

    public void testShortValue() {
        assertEquals(Short.valueOf((short)1234), service.getShortValue());
    }

    public void testIntegerValue() {
        assertEquals(Integer.valueOf(12345678), service.getIntegerValue());
    }

    public void testLongValue() {
        assertEquals(Long.valueOf(123451234512345l), service.getLongValue());
    }

    public void testFloatValue() {
        assertEquals(1.2345f, service.getFloatValue());
    }

    public void testDoubleValue() {
        assertEquals(1.2345e10, service.getDoubleValue());
    }

    public void testClassValue() {
        assertEquals(PropertyTypes.class, service.getClassValue());
    }

    public void testURI() {
        assertEquals(URI.create("urn:sca4j:test"), service.getUriValue());
    }

    public void testURL() throws MalformedURLException {
        assertEquals(new URL("file://./root"), service.getUrlValue());
    }

    public void testDate() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.clear();
//        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(2007, Calendar.OCTOBER, 31, 0, 0, 0);
        assertEquals(calendar.getTime(), service.getDateValue());
    }

    public void testCalendar() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.clear();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(2007, Calendar.OCTOBER, 31, 1, 0, 0);
        
        assertEquals(calendar.getTime(), service.getCalendarValue().getTime());
    }

/*
    public void testIntArray() {
        assertTrue(Arrays.equals(new int[]{1,2,3}, service.getIntArray()));
    }
*/

    public void testMap() {
        Map<String, String> map = service.getMapValue();
        assertEquals("1", map.get("one"));
        assertEquals("2", map.get("two"));
    }

    public void testProperties() {
        Properties properties = service.getPropertiesValue();
        assertEquals("value1", properties.getProperty("prop1"));
        assertEquals("value2", properties.getProperty("prop2"));
    }
    
    public void testList() {
        List<String> list = service.getListValue();
        assertEquals("value1", list.get(0));
        assertEquals("value2", list.get(1));
    }
    
    public void testMapOfQNameToClass() {
        Map<QName, Class<?>> map = service.getMapOfQNameToClassValue();
        assertEquals(map.get(new QName("urn:foo", "one")), String.class);
        assertEquals(map.get(new QName("urn:foo", "two")), Date.class);
    }
}
