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

import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Property;

/**
 * @version $Rev: 3152 $ $Date: 2008-03-21 23:00:25 +0000 (Fri, 21 Mar 2008) $
 */
public class PublicFieldPropertyTypesImpl implements PropertyTypes {
    @Property public boolean booleanPrimitive;
    @Property public byte bytePrimitive;
    @Property public short shortPrimitive;
    @Property public int intPrimitive;
    @Property public long longPrimitive;
    @Property public float floatPrimitive;
    @Property public double doublePrimitive;

    @Property public Boolean booleanValue;
    @Property public Byte byteValue;
    @Property public Short shortValue;
    @Property public Integer integerValue;
    @Property public Long longValue;
    @Property public Float floatValue;
    @Property public Double doubleValue;
    @Property public Class<?> classValue;

    @Property public String string;
    @Property public URI uriValue;
    @Property public URL urlValue;
    @Property public Date dateValue;
    @Property public Calendar calendarValue;

    @Property public int[] intArray;
    @Property public Map<String, String> mapValue;
    @Property public Properties propertiesValue;
    @Property public List<String> listValue;
    @Property public Map<QName, Class<?>> mapOfQNameToClassValue;

    public boolean getBooleanPrimitive() {
        return booleanPrimitive;
    }

    public byte getBytePrimitive() {
        return bytePrimitive;
    }

    public short getShortPrimitive() {
        return shortPrimitive;
    }

    public int getIntPrimitive() {
        return intPrimitive;
    }

    public long getLongPrimitive() {
        return longPrimitive;
    }

    public float getFloatPrimitive() {
        return floatPrimitive;
    }

    public double getDoublePrimitive() {
        return doublePrimitive;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public Byte getByteValue() {
        return byteValue;
    }

    public Short getShortValue() {
        return shortValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public Class<?> getClassValue() {
        return classValue;
    }

    public String getString() {
        return string;
    }

    public URI getUriValue() {
        return uriValue;
    }

    public URL getUrlValue() {
        return urlValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public Calendar getCalendarValue() {
        return calendarValue;
    }

    public int[] getIntArray() {
        return intArray;
    }

    public Map<String, String> getMapValue() {
        return mapValue;
    }

    public Properties getPropertiesValue() {
        return propertiesValue;
    }
    
    public List<String> getListValue() {
        return listValue;
    }
    
    public Map<QName, Class<?>> getMapOfQNameToClassValue() {
        return mapOfQNameToClassValue;
    }
}
