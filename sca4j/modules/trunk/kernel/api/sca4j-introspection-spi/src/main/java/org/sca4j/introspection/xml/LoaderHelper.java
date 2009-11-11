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
package org.sca4j.introspection.xml;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.scdl.PolicyAware;
import org.w3c.dom.Document;

/**
 * Interface for the helper class for loading intents and policy sets into elements aginst which intents and policies can be declared.
 *
 * @version $Revision$ $Date$
 */
public interface LoaderHelper {    
	
   /**
    * Load the value of the attribute key from the current element.
    *
    * @param reader a stream containing a property value
    * @return a standalone document containing the value
    * @throws javax.xml.stream.XMLStreamException
    *          if there was a problem reading the stream
    */
   Document loadKey(XMLStreamReader reader);
	 	 	
    /**
     * Load an XML value from a Stax stream.
     * <p/>
     * The reader must be positioned at an element whose body contains an XML value. This will typically be an SCA &lt;property&gt; element (either in
     * a &lt;composite&gt; or in a &lt;component&gt;). The resulting document comprises a &lt;value&gt; element whose body content will be that of the
     * original &lt;property&gt; element.
     *
     * @param reader a stream containing a property value
     * @return a standalone document containing the value
     * @throws javax.xml.stream.XMLStreamException
     *          if there was a problem reading the stream
     */
    Document loadValue(XMLStreamReader reader) throws XMLStreamException;

    /**
     * Loads policy sets and intents defined against bindings, implementations, services, references and components. Errors will be collated in the
     * IntrospectionContext.
     *
     * @param policyAware Element against which policy sets and intents are declared.
     * @param reader      XML stream reader from where the attributes are read.
     * @param context     the introspection context.
     */
    void loadPolicySetsAndIntents(PolicyAware policyAware, XMLStreamReader reader, IntrospectionContext context);

    /**
     * Convert a component URI in the form ${componentName}/${serviceName} to a URI of the form ${componentName}#${serviceName}
     *
     * @param target the target URI to convert
     * @return a URI where the fragment represents the service name
     */
    URI getURI(String target);

    /**
     * Parses a list of qualified names.
     *
     * @param reader    XML stream reader.
     * @param attribute Attribute that contains the list of qualified names.
     * @return Set containing the qualified names.
     * @throws InvalidPrefixException If the qualified name cannot be resolved.
     */
    List<QName> parseListOfQNames(XMLStreamReader reader, String attribute) throws InvalidPrefixException;

    /**
     * Constructs a QName from the given name. If a namespace prefix is not specified in the name, the namespace context is used
     *
     * @param name   the name to parse
     * @param reader the XML stream reader
     * @return the parsed QName
     * @throws InvalidPrefixException if a specified namespace prefix is invalid
     */
    QName createQName(String name, XMLStreamReader reader) throws InvalidPrefixException;

    /**
     * Parses a list of URIs contained in a attribute.
     *
     * @param reader    the XML stream reader
     * @param attribute the attribute to parse
     * @return the list of URIs contained in that attribute, or null if the attribute is not present
     */
    List<URI> parseListOfUris(XMLStreamReader reader, String attribute);
}
