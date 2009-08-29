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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.transform.dom2java.generics.list;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.xml.namespace.QName;

import org.w3c.dom.Node;

import org.sca4j.scdl.DataType;
import org.sca4j.spi.model.type.JavaParameterizedType;
import org.sca4j.transform.AbstractPullTransformer;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;

/**
 * Converts a String value to a list of QNames. Expects the property to be defined in the format,
 * <p/>
 * <code> value1, value2, value3 </code>
 * <p/>
 * where values correspond to the format specified by {@link QName#valueOf(String)}.
 *
 * @version $Rev: 1570 $ $Date: 2007-10-20 14:24:19 +0100 (Sat, 20 Oct 2007) $
 */
public class String2ListOfQName extends AbstractPullTransformer<Node, List<QName>> {

    private static List<QName> FIELD = null;
    private static JavaParameterizedType TARGET = null;

    static {
        try {
            ParameterizedType parameterizedType = (ParameterizedType) String2ListOfQName.class.getDeclaredField("FIELD").getGenericType();
            TARGET = new JavaParameterizedType(parameterizedType);
        } catch (NoSuchFieldException ignore) {
        }
    }

    public DataType<?> getTargetType() {
        return TARGET;
    }

    public List<QName> transform(final Node node, final TransformContext context) throws TransformationException {

        final List<QName> list = new ArrayList<QName>();
        final StringTokenizer tokenizer = new StringTokenizer(node.getTextContent(), " \t\n\r\f,");

        while (tokenizer.hasMoreElements()) {
            list.add(QName.valueOf(tokenizer.nextToken()));
        }

        return list;

    }

}
