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

import org.oasisopen.sca.annotation.Property;

/**
 * @version $Rev: 3152 $ $Date: 2008-03-21 23:00:25 +0000 (Fri, 21 Mar 2008) $
 */
public class ProtectedSetterPropertyTypesImpl implements PropertyTypes {
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

    @Property(required=false)
    protected void setBooleanPrimitive(boolean booleanPrimitive) {
        this.booleanPrimitive = booleanPrimitive;
    }

    public byte getBytePrimitive() {
        return bytePrimitive;
    }

    @Property(required=false)
    protected void setBytePrimitive(byte bytePrimitive) {
        this.bytePrimitive = bytePrimitive;
    }

    public short getShortPrimitive() {
        return shortPrimitive;
    }

    @Property(required=false)
    protected void setShortPrimitive(short shortPrimitive) {
        this.shortPrimitive = shortPrimitive;
    }

    public int getIntPrimitive() {
        return intPrimitive;
    }

    @Property(required=false)
    protected void setIntPrimitive(int intPrimitive) {
        this.intPrimitive = intPrimitive;
    }

    public long getLongPrimitive() {
        return longPrimitive;
    }

    @Property(required=false)
    protected void setLongPrimitive(long longPrimitive) {
        this.longPrimitive = longPrimitive;
    }

    public float getFloatPrimitive() {
        return floatPrimitive;
    }

    @Property(required=false)
    protected void setFloatPrimitive(float floatPrimitive) {
        this.floatPrimitive = floatPrimitive;
    }

    public double getDoublePrimitive() {
        return doublePrimitive;
    }

    @Property(required=false)
    protected void setDoublePrimitive(double doublePrimitive) {
        this.doublePrimitive = doublePrimitive;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    @Property(required=false)
    protected void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Byte getByteValue() {
        return byteValue;
    }

    @Property(required=false)
    protected void setByteValue(Byte byteValue) {
        this.byteValue = byteValue;
    }

    public Short getShortValue() {
        return shortValue;
    }

    @Property(required=false)
    protected void setShortValue(Short shortValue) {
        this.shortValue = shortValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    @Property(required=false)
    protected void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    @Property(required=false)
    protected void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    @Property(required=false)
    protected void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    @Property(required=false)
    protected void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Class<?> getClassValue() {
        return classValue;
    }

    @Property(required=false)
    protected void setClassValue(Class<?> classValue) {
        this.classValue = classValue;
    }

    public String getString() {
        return string;
    }

    @Property(required=false)
    protected void setString(String string) {
        this.string = string;
    }

    public URI getUriValue() {
        return uriValue;
    }

    @Property(required=false)
    protected void setUriValue(URI uriValue) {
        this.uriValue = uriValue;
    }

    public URL getUrlValue() {
        return urlValue;
    }

    @Property(required=false)
    protected void setUrlValue(URL urlValue) {
        this.urlValue = urlValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    @Property(required=false)
    protected void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Calendar getCalendarValue() {
        return calendarValue;
    }

    @Property(required=false)
    protected void setCalendarValue(Calendar calendarValue) {
        this.calendarValue = calendarValue;
    }

    public int[] getIntArray() {
        return intArray;
    }

    @Property(required=false)
    protected void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }

    public Map<String, String> getMapValue() {
        return mapValue;
    }

    @Property(required=false)
    protected void setMapValue(Map<String, String> mapValue) {
        this.mapValue = mapValue;
    }

    public Properties getPropertiesValue() {
        return propertiesValue;
    }

    @Property(required=false)
    protected void setPropertiesValue(Properties propertiesValue) {
        this.propertiesValue = propertiesValue;
    }
    
    @Property(required=false)
    protected void setListValue(List<String> listValue) {
        this.listValue = listValue;
    }
    
    public List<String> getListValue() {
        return listValue;
    }
    
    @Property(required=false)
    protected void setMapOfQNameToClassValue(Map<QName, Class<?>> mapOfQNameToClassValue) {
        this.mapOfQNameToClassValue = mapOfQNameToClassValue;
    }
    
    public Map<QName, Class<?>> getMapOfQNameToClassValue() {
        return mapOfQNameToClassValue;
    }
}
