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
package org.sca4j.introspection.java;

import org.sca4j.host.contribution.ValidationFailure;
import org.sca4j.scdl.InjectableAttributeType;
import org.sca4j.scdl.InjectionSite;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.MethodInjectionSite;
import org.sca4j.scdl.ConstructorInjectionSite;

/**
 * Denotes an unknown InjectableAttributeType.
 *
 * @version $Rev: 4336 $ $Date: 2008-05-25 10:06:15 +0100 (Sun, 25 May 2008) $
 */
public class UnknownInjectionType extends ValidationFailure<InjectionSite> {
    private InjectableAttributeType type;
    private String clazz;

    public UnknownInjectionType(InjectionSite site, InjectableAttributeType type, String clazz) {
        super(site);
        this.type = type;
        this.clazz = clazz;
    }

    public String getImplementationClass() {
        return clazz;
    }

    public String getMessage() {
        InjectionSite site = getValidatable();
        if (site instanceof FieldInjectionSite) {
            FieldInjectionSite field = (FieldInjectionSite) site;
            return "Unknow injection type " + type + " on field " + field.getName() + " in class " + clazz;
        } else if (site instanceof MethodInjectionSite) {
            MethodInjectionSite method = (MethodInjectionSite) site;
            return "Unknow injection type " + type + " on method " + method.getSignature() + " in class " + clazz;
        } else if (site instanceof ConstructorInjectionSite) {
            ConstructorInjectionSite ctor = (ConstructorInjectionSite) site;
            return "Unknow injection type " + type + " on constructor " + ctor.getSignature() + " in class " + clazz;
        } else {
            return "Unknow injection type " + type + " found in class " + clazz;
        }
    }
}
