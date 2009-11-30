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
package org.sca4j.groovy.scdl;

import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.Implementation;

/**
 * A component implemented in Groovy. The implementation can be a script in source or compiled form.
 *
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public class GroovyImplementation extends Implementation<PojoComponentType> {
    private static final long serialVersionUID = -8092204063300139457L;
    public static final QName IMPLEMENTATION_GROOVY = new QName(Namespaces.SCA4J_NS, "groovy");

    private String scriptName;
    private String className;

    public GroovyImplementation() {
    }

    public GroovyImplementation(String scriptName, String className) {
        this.scriptName = scriptName;
        this.className = className;
    }

    public GroovyImplementation(String scriptName, String className, PojoComponentType componentType) {
        super(componentType);
        this.scriptName = scriptName;
        this.className = className;
    }

    public QName getType() {
        return IMPLEMENTATION_GROOVY;
    }

    /**
     * Returns the name of a file containing the script source.
     *
     * @return the name of a file containing the script source
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * Sets the name of a file containing the script source.
     *
     * @param scriptName the name of a file containing the script source
     */
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    /**
     * Returns the name of a compiled Groovy class.
     *
     * @return the name of a compiled Groovy class
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the name of a compiled Groovy class.
     *
     * @param className the name of a compiled Groovy class
     */
    public void setClassName(String className) {
        this.className = className;
    }
}