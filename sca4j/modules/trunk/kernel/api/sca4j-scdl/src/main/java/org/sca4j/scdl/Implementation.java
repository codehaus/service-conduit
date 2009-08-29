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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.scdl;

import javax.xml.namespace.QName;

import org.sca4j.scdl.validation.MissingComponentType;

/**
 * Represents a component implementation
 *
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public abstract class Implementation<T extends AbstractComponentType<?, ?, ?, ?>> extends AbstractPolicyAware {
    private static final long serialVersionUID = -6060603636927660850L;
    private T componentType;

    protected Implementation() {
    }

    protected Implementation(T componentType) {
        this.componentType = componentType;
    }

    public T getComponentType() {
        return componentType;
    }

    public void setComponentType(T componentType) {
        this.componentType = componentType;
    }

    /**
     * Returns true if this implementation corresponds to the supplied XML element.
     *
     * @param type the QName of the implementation element
     * @return true if this instance is of the supplied type
     */
    public boolean isType(QName type) {
        return getType().equals(type);
    }

    /**
     * Returns true if this implementation is a composite.
     * <p/>
     * This indicates whether this implementation can have children in the logical assembly.
     *
     * @return true if this implementation is a composite
     */
    public boolean isComposite() {
        return false;
    }

    /**
     * Returns the SCDL XML element corresponding to this type.
     *
     * @return the SCDL XML element corresponding to this type
     */
    public abstract QName getType();

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);
        if (componentType == null) {
            context.addError(new MissingComponentType(this));
        } else {
            componentType.validate(context);
        }
    }
}
