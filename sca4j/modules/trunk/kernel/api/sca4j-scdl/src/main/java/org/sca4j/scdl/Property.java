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

import org.w3c.dom.Document;

/**
 * A component property as declared in the component type.
 *
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public class Property extends ModelObject {
    private static final long serialVersionUID = -1930360315004829917L;
    private String name;
    private boolean many;
    private boolean required;
    private QName xmlType;

    private Document defaultValue;

    public Property() {
    }

    public Property(String name, QName xmlType) {
        this.name = name;
        this.xmlType = xmlType;
    }

    /**
     * Returns the name of the property.
     *
     * @return the name of the property
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the property.
     *
     * @param name the name of the property
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns whether the property is many-valued or single-valued.
     *
     * @return true if the property is many-valued
     */
    public boolean isMany() {
        return many;
    }

    /**
     * Sets whether the property is many-valued or single-valued.
     *
     * @param many true if the property is many-valued
     */
    public void setMany(boolean many) {
        this.many = many;
    }

    /**
     * Returns whether the component definition must supply a value for this property.
     *
     * @return true if the component definition must supply a value
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets whether the component definition must supply a value for this property.
     *
     * @param required true if the component definition must supply a value
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    public QName getXmlType() {
        return xmlType;
    }

    public void setXmlType(QName xmlType) {
        this.xmlType = xmlType;
    }

    public Document getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Document defaultValue) {
        this.defaultValue = defaultValue;
    }
}
