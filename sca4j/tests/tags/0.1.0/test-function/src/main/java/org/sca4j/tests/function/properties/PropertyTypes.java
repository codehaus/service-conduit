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
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

/**
 * Test service for returning Properties with different types.
 *
 * @version $Rev: 3152 $ $Date: 2008-03-21 23:00:25 +0000 (Fri, 21 Mar 2008) $
 */
public interface PropertyTypes {
    boolean getBooleanPrimitive();

    byte getBytePrimitive();

    short getShortPrimitive();

    int getIntPrimitive();

    long getLongPrimitive();

    float getFloatPrimitive();

    double getDoublePrimitive();

    Boolean getBooleanValue();

    Byte getByteValue();

    Short getShortValue();

    Integer getIntegerValue();

    Long getLongValue();

    Float getFloatValue();

    Double getDoubleValue();

    Class<?> getClassValue();

    String getString();

    URI getUriValue();

    URL getUrlValue();

    Date getDateValue();

    Calendar getCalendarValue();

    int[] getIntArray();

    Map<String, String> getMapValue();

    Properties getPropertiesValue();
    
    List<String> getListValue();
    
    Map<QName, Class<?>> getMapOfQNameToClassValue();
    
}
