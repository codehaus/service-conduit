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

import java.net.URI;

import org.w3c.dom.Document;

/**
 * Represents the value of a configured component property. The value can be specified: <ul> <li>as an XML value in the
 * component definition</li> <li>as a reference to an external resource<li> <li>as the result of an XPath
 * expression</li> <ul>
 *
 * @version $Rev: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul 2008) $
 */
public class PropertyValue extends ModelObject {
    private static final long serialVersionUID = -1638553201072873854L;
    private String name;
    private String source;
    private URI file;
    private DataType valueType;
    private Document value;

    public PropertyValue() {
    }

    /**
     * Constructor specifying the name of a property and the XPath source expression.
     *
     * @param name   the name of the property which this value is for
     * @param source an XPath expression whose result will be the actual value
     */
    public PropertyValue(String name, String source) {
        this.name = name;
        this.source = source;
    }

    /**
     * Constructor specifying the name of a property loaded from an exteral resource.
     *
     * @param name the name of the property which this value is for
     * @param file A URI that the property value can be loaded from
     */
    public PropertyValue(String name, URI file) {
        this.name = name;
        this.file = file;
    }

    /**
     * @param name      the name of the property
     * @param valueType the XML type of the value
     * @param value     the property value
     */
    public PropertyValue(String name, DataType valueType, Document value) {
        this.name = name;
        this.valueType = valueType;
        this.value = value;
    }

    /**
     * Returns the name of the property that this value is for.
     *
     * @return the name of the property that this value is for
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the property that this value is for.
     *
     * @param name the name of the property that this value is for
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns an XPath expression that should be evaluated to get the actual property value.
     *
     * @return an XPath expression that should be evaluated to get the actual property value
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets an XPath expression that should be evaluated to get the actual property value.
     *
     * @param source an XPath expression that should be evaluated to get the actual property value
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Returns the location of the resource containing the property value.
     *
     * @return the location of the resource containing the property value
     */
    public URI getFile() {
        return file;
    }

    /**
     * Sets the location of the resource containing the property value
     *
     * @param file the location of the resource containing the property value
     */
    public void setFile(URI file) {
        this.file = file;
    }

    /**
     * Returns the XML value of the property.
     *
     * @return the XML value of the property
     */
    public Document getValue() {
        return value;
    }

    /**
     * Sets the XML value of the property.
     *
     * @param value the XML value of the property
     */
    public void setValue(Document value) {
        this.value = value;
    }

    /**
     * Returns the value's XML Schema type.
     *
     * @return the value's XML Schema type
     */
    public DataType getValueType() {
        return valueType;
    }

    /**
     * Sets the value's XML Schema type.
     *
     * @param valueType the value's XML Schema type
     */
    public void setValueType(DataType valueType) {
        this.valueType = valueType;
    }
}
