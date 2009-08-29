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
