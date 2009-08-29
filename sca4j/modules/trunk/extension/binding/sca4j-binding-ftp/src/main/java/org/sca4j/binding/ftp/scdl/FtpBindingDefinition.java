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
package org.sca4j.binding.ftp.scdl;

import java.net.URI;

import org.sca4j.binding.ftp.common.Constants;
import org.sca4j.scdl.BindingDefinition;
import org.w3c.dom.Document;

/**
 * Binding definition loaded from the SCDL.
 * 
 * @version $Revision$ $Date$
 */
public class FtpBindingDefinition extends BindingDefinition {
    
    private static final long serialVersionUID = -889044951554792780L;

    private final TransferMode transferMode;
    private String tmpFileSuffix;

    /**
     * Initializes the binding type.
     *
     * @param uri          Target URI.
     * @param transferMode the FTP transfer mode
     * @param key          the key for mapped bindings
     */
    public FtpBindingDefinition(URI uri, TransferMode transferMode, Document key) {
        super(uri, Constants.BINDING_QNAME, key);
        this.transferMode = transferMode;
    }

    /**
     * Gets the transfer mode.
     *
     * @return File transfer mode.
     */
    public TransferMode getTransferMode() {
        return transferMode;
    }

    /**
     * Gets the temporary file suffix to be used while file in transmission (i.e. during STOR operation).
     * 
     * @return temporary file suffix
     */
    public String getTmpFileSuffix() {
        return tmpFileSuffix;
    }

    /**
     * Sets the temporary file suffix to be used while file in transmission (i.e. during STOR operation).
     * 
     * @param tmpFileSuffix temporary file suffix
     */
    public void setTmpFileSuffix(String tmpFileSuffix) {
        this.tmpFileSuffix = tmpFileSuffix;
    }
    
}
