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
public class PublicSetterPropertyTypesImpl implements PropertyTypes {
    private boolean booleanPrimitive;
    private byte bytePrimitive;
    private short shortPrimitive;
    private int intPrimitive;
    private long longPrimitive;
    private float floatPrimitive;
    private double doublePrimitive;

    private Boolean booleanValue;
    private Byte byteValue;
    private Short shortValue;
    private Integer integerValue;
    private Long longValue;
    private Float floatValue;
    private Double doubleValue;
    private Class<?> classValue;

    private String string;
    private URI uriValue;
    private URL urlValue;
    private Date dateValue;
    private Calendar calendarValue;

    private int[] intArray;
    private Map<String, String> mapValue;
    private Properties propertiesValue;
    private List<String> listValue;
    private Map<QName, Class<?>> mapOfQNameToClassValue;

    public boolean getBooleanPrimitive() {
        return booleanPrimitive;
    }

    @Property
    public void setBooleanPrimitive(boolean booleanPrimitive) {
        this.booleanPrimitive = booleanPrimitive;
    }

    public byte getBytePrimitive() {
        return bytePrimitive;
    }

    @Property
    public void setBytePrimitive(byte bytePrimitive) {
        this.bytePrimitive = bytePrimitive;
    }

    public short getShortPrimitive() {
        return shortPrimitive;
    }

    @Property
    public void setShortPrimitive(short shortPrimitive) {
        this.shortPrimitive = shortPrimitive;
    }

    public int getIntPrimitive() {
        return intPrimitive;
    }

    @Property
    public void setIntPrimitive(int intPrimitive) {
        this.intPrimitive = intPrimitive;
    }

    public long getLongPrimitive() {
        return longPrimitive;
    }

    @Property
    public void setLongPrimitive(long longPrimitive) {
        this.longPrimitive = longPrimitive;
    }

    public float getFloatPrimitive() {
        return floatPrimitive;
    }

    @Property
    public void setFloatPrimitive(float floatPrimitive) {
        this.floatPrimitive = floatPrimitive;
    }

    public double getDoublePrimitive() {
        return doublePrimitive;
    }

    @Property
    public void setDoublePrimitive(double doublePrimitive) {
        this.doublePrimitive = doublePrimitive;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    @Property
    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Byte getByteValue() {
        return byteValue;
    }

    @Property
    public void setByteValue(Byte byteValue) {
        this.byteValue = byteValue;
    }

    public Short getShortValue() {
        return shortValue;
    }

    @Property
    public void setShortValue(Short shortValue) {
        this.shortValue = shortValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    @Property
    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    @Property
    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    @Property
    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    @Property
    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Class<?> getClassValue() {
        return classValue;
    }

    @Property
    public void setClassValue(Class<?> classValue) {
        this.classValue = classValue;
    }

    public String getString() {
        return string;
    }

    @Property
    public void setString(String string) {
        this.string = string;
    }

    public URI getUriValue() {
        return uriValue;
    }

    @Property
    public void setUriValue(URI uriValue) {
        this.uriValue = uriValue;
    }

    public URL getUrlValue() {
        return urlValue;
    }

    @Property
    public void setUrlValue(URL urlValue) {
        this.urlValue = urlValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    @Property
    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Calendar getCalendarValue() {
        return calendarValue;
    }

    @Property
    public void setCalendarValue(Calendar calendarValue) {
        this.calendarValue = calendarValue;
    }

    public int[] getIntArray() {
        return intArray;
    }

    @Property
    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }

    public Map<String, String> getMapValue() {
        return mapValue;
    }

    @Property
    public void setMapValue(Map<String, String> mapValue) {
        this.mapValue = mapValue;
    }

    public Properties getPropertiesValue() {
        return propertiesValue;
    }

    @Property
    public void setPropertiesValue(Properties propertiesValue) {
        this.propertiesValue = propertiesValue;
    }
    
    @Property
    public void setListValue(List<String> listValue) {
        this.listValue = listValue;
    }
    
    public List<String> getListValue() {
        return listValue;
    }
    
    @Property
    public void setMapOfQNameToClassValue(Map<QName, Class<?>> mapOfQNameToClassValue) {
        this.mapOfQNameToClassValue = mapOfQNameToClassValue;
    }
    
    public Map<QName, Class<?>> getMapOfQNameToClassValue() {
        return mapOfQNameToClassValue;
    }
}
