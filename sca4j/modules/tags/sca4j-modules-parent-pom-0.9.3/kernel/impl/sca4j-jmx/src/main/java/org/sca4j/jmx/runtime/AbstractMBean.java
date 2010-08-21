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
package org.sca4j.jmx.runtime;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.JMException;
import javax.management.MBeanInfo;

/**
 * @version $Rev: 3690 $ $Date: 2008-04-22 20:06:52 +0100 (Tue, 22 Apr 2008) $
 */
public abstract class AbstractMBean implements DynamicMBean {
    protected final MBeanInfo mbeanInfo;

    public AbstractMBean(MBeanInfo mbeanInfo) {
        this.mbeanInfo = mbeanInfo;
    }

    public MBeanInfo getMBeanInfo() {
        return mbeanInfo;
    }

    public AttributeList getAttributes(String[] strings) {
        AttributeList list = new AttributeList(strings.length);
        for (String s : strings) {
            try {
                Object value = getAttribute(s);
                list.add(new Attribute(s, value));
            } catch (JMException e) {
                // ignore exceptions which means the attribute won't be in the result
            }
        }
        return list;
    }

    public AttributeList setAttributes(AttributeList attributeList) {
        AttributeList result = new AttributeList(attributeList.size());
        for (Object o : attributeList) {
            Attribute attribute = (Attribute) o;
            try {
                setAttribute(attribute);
            } catch (JMException e) {
                // ignore exceptions which means the attribute won't be in the result
            }
        }
        return result;
    }
}
