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
package org.sca4j.binding.jms.scdl;

import java.net.URI;

import org.sca4j.binding.jms.common.JmsBindingMetadata;
import org.sca4j.binding.jms.introspection.JmsBindingLoader;
import org.sca4j.scdl.BindingDefinition;
import org.w3c.dom.Document;

/**
 * Logical model object for JMS binding definition. TODO Support for overriding
 * request connection, response connection and operation properties from a
 * definition document as well as activation spec and resource adaptor.
 * 
 * @version $Revision: 5070 $ $Date: 2008-07-21 17:52:37 +0100 (Mon, 21 Jul
 *          2008) $
 */
public class JmsBindingDefinition extends BindingDefinition {
    private static final long serialVersionUID = -1888120511695824132L;

    /***
     * A generated URI overriding TargetUri in base class.
     */
    private URI generatedTargetUri;

    /**
     * JMS binding metadata shared between logical and physical.
     */
    private JmsBindingMetadata metadata;

    /**
     * @param metadata Metadata to be initialized.
     */
    public JmsBindingDefinition(JmsBindingMetadata metadata, Document key) {
        super(null, JmsBindingLoader.BINDING_QNAME, key);
        this.metadata = metadata;
    }

    /**
     * @param targetURI URI of binding target
     * @param metadata Metadata to be initialized.
     */
    public JmsBindingDefinition(URI targetURI, JmsBindingMetadata metadata, Document key) {
        super(targetURI, JmsBindingLoader.BINDING_QNAME, key);
        this.metadata = metadata;
    }

    /**
     * @return the metadata
     */
    public JmsBindingMetadata getMetadata() {
        return metadata;
    }

    /**
     * @param metadata the metadata to set
     */
    public void setMetadata(JmsBindingMetadata metadata) {
        this.metadata = metadata;
    }

    public void setGeneratedTargetUri(URI generatedTargetUri) {
        this.generatedTargetUri = generatedTargetUri;
    }

    @Override
    public URI getTargetUri() {
        return generatedTargetUri;
    }

}
