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
public class PublicConstructorPropertyTypesImpl implements PropertyTypes {
    private final boolean booleanPrimitive;
    private final byte bytePrimitive;
    private final short shortPrimitive;
    private final int intPrimitive;
    private final long longPrimitive;
    private final float floatPrimitive;
    private final double doublePrimitive;

    private final Boolean booleanValue;
    private final Byte byteValue;
    private final Short shortValue;
    private final Integer integerValue;
    private final Long longValue;
    private final Float floatValue;
    private final Double doubleValue;
    private final Class<?> classValue;

    private final String string;
    private final URI uriValue;
    private final URL urlValue;
    private final Date dateValue;
    private final Calendar calendarValue;

    private final int[] intArray;
    private final Map<String, String> mapValue;
    private final Properties propertiesValue;
    private final List<String> listValue;
    private final Map<QName, Class<?>> mapOfQNameToClassValue;

    public PublicConstructorPropertyTypesImpl(@Property(name = "booleanPrimitive")boolean booleanPrimitive,
                                              @Property(name = "bytePrimitive")byte bytePrimitive,
                                              @Property(name = "shortPrimitive")short shortPrimitive,
                                              @Property(name = "intPrimitive")int intPrimitive,
                                              @Property(name = "longPrimitive")long longPrimitive,
                                              @Property(name = "floatPrimitive")float floatPrimitive,
                                              @Property(name = "doublePrimitive")double doublePrimitive,
                                              @Property(name = "booleanValue")Boolean booleanValue,
                                              @Property(name = "byteValue")Byte byteValue,
                                              @Property(name = "shortValue")Short shortValue,
                                              @Property(name = "integerValue")Integer integerValue,
                                              @Property(name = "longValue")Long longValue,
                                              @Property(name = "floatValue")Float floatValue,
                                              @Property(name = "doubleValue")Double doubleValue,
                                              @Property(name = "classValue")Class<?> classValue,
                                              @Property(name = "string")String string,
                                              @Property(name = "uriValue")URI uriValue,
                                              @Property(name = "urlValue")URL urlValue,
                                              @Property(name = "dateValue")Date dateValue,
                                              @Property(name = "calendarValue")Calendar calendarValue,
                                              @Property(name = "intArray") int[] intArray,
                                              @Property(name = "mapValue") Map<String, String> mapValue,
                                              @Property(name = "propertiesValue") Properties propertiesValue,
                                              @Property(name = "listValue") List<String> listValue,
                                              @Property(name = "mapOfQNameToClassValue") Map<QName, Class<?>> mapOfQNameToClassValue) {
        this.booleanPrimitive = booleanPrimitive;
        this.bytePrimitive = bytePrimitive;
        this.shortPrimitive = shortPrimitive;
        this.intPrimitive = intPrimitive;
        this.longPrimitive = longPrimitive;
        this.floatPrimitive = floatPrimitive;
        this.doublePrimitive = doublePrimitive;
        this.booleanValue = booleanValue;
        this.byteValue = byteValue;
        this.shortValue = shortValue;
        this.integerValue = integerValue;
        this.longValue = longValue;
        this.floatValue = floatValue;
        this.doubleValue = doubleValue;
        this.classValue = classValue;
        this.string = string;
        this.uriValue = uriValue;
        this.urlValue = urlValue;
        this.dateValue = dateValue;
        this.calendarValue = calendarValue;
        this.intArray = intArray;
        this.mapValue = mapValue;
        this.propertiesValue = propertiesValue;
        this.listValue = listValue;
        this.mapOfQNameToClassValue = mapOfQNameToClassValue;
    }

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
