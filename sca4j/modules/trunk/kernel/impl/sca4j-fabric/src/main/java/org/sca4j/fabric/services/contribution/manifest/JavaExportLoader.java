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
package org.sca4j.fabric.services.contribution.manifest;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.oasisopen.sca.annotation.EagerInit;
import org.sca4j.fabric.services.contribution.MissingPackage;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.TypeLoader;

/**
 * Loads an <code>export.java</code> entry in a contribution manifest.
 *
 * @version $Rev: 4301 $ $Date: 2008-05-23 06:33:58 +0100 (Fri, 23 May 2008) $
 */
@EagerInit
public class JavaExportLoader implements TypeLoader<JavaExport> {
    //private static final QName EXPORT = new QName(SCA_NS, "export.java");


    public JavaExport load(XMLStreamReader reader, IntrospectionContext context) throws XMLStreamException {
        String packageName = reader.getAttributeValue(null, "package");
        if (packageName == null) {
            MissingPackage failure = new MissingPackage("No package name specified", reader);
            context.addError(failure);
            return null;
        }
        return new JavaExport(packageName);
    }
}
